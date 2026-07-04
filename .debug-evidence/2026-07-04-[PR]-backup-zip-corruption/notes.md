# Klick'r backups corrupted due to lack of file truncation when overwriting

## Environment

- Device: Xiaomi 11X Pro (Android 14)
- Package Name: `com.buzbuz.smartautoclicker.debug`
- Branch: `wip/upstream-bug-triage`

## Observed behavior

- Overwriting an existing backup file on the device results in a corrupted ZIP archive.
- Extracting/verifying the corrupted ZIP using standard zip extractors reports integrity test failures (e.g., "ZIP entry format error", "Local File Header signature not found", "DEFLATE stream incomplete").

## Confirmed evidence

- Analyzing `truncation_test.zip` (981,821 bytes) pulled from the Xiaomi 11X Pro:
  - Found exactly **two** EOCD (End of Central Directory) signatures (`PK\x05\x06`) in the binary data at offsets `[114237, 981799]`.
  - This confirms that the new ZIP data (114,259 bytes) was written starting at offset 0, but the remaining 867 KB from the old ZIP was left trailing at the end of the file.
  - Decompression of entries near the transition boundary fails because old Central Directory records point to the newly overwritten bytes at the beginning of the file, resulting in file name mismatches (e.g., CDR expects `21/` but reads `20/`).

## Root Cause

- In `BackupEngine.kt`, the backup file's output stream is opened using:
  ```kotlin
  contentResolver.openOutputStream(zipFileUri)
  ```
- By default, this opens the stream in `"w"` (write-only) mode.
- In Android's Storage Access Framework (`DocumentsProvider`), some implementations (e.g., Xiaomi's DownloadsProvider) do NOT truncate files when opened in write-only (`"w"`) mode.
- Additionally, `progress.onCompleted` is called inside the `.use` block of the `ZipOutputStream`, which triggers the completion callback before the ZIP stream is fully flushed, written, and closed.

## Proposed Fix

- Open the output stream with the explicit `"wt"` (write-truncate) mode:
  ```kotlin
  contentResolver.openOutputStream(zipFileUri, "wt")
  ```
- Move `progress.onCompleted(...)` to be outside the `.use` block so it runs only after the `ZipOutputStream` has successfully written the Central Directory and EOCD and closed.
