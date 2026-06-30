# Klick'r backups appear as 0 bytes in Android Files and transfer empty through LocalSend

## Observed behavior

- Google/AOSP DocumentsUI (`com.google.android.documentsui`) displays Klick'r-created ZIP backups as `0 B`.
- The files remain importable by Klick'r on the phone.
- Sending one through LocalSend produces an actual zero-byte file on Windows.
- The issue affects backups from several dates, so it is not limited to one newly created archive.

## Confirmed evidence

Screenshot: `files-app-zero-byte-backups.png`

The screenshot shows:

- `detection area bug.zip`: `0 B`
- `one only.zip`: `0 B`
- `Backup to Klick'r.zip`: `0 B`

The underlying phone files are not empty:

- `detection area bug.zip`: 2,806 bytes
- `one only.zip`: 518,330 bytes
- `Backup to Klick'r.zip`: 2,746,853 bytes

Android MediaStore also reports the correct non-zero sizes. For example, its record for `detection area bug.zip` reports `_size=2806` and `mime_type=application/zip`.

On Windows, LocalSend-created copies were genuinely empty, including:

- `detection area bug.zip`: 0 bytes
- `made with klickr.zip`: 0 bytes
- `SmartAutoClicker-Backup (7).zip`: 0 bytes

This establishes a metadata split: the actual file and MediaStore know the correct size, while DocumentsUI exposes or caches a zero-byte document size. LocalSend appears to trust that zero size and transfers no payload.

## Relevant app behavior

`BackupEngine.createBackup` creates the ZIP with:

```kotlin
ZipOutputStream(contentResolver.openOutputStream(zipFileUri)).use { zipStream ->
    // Write scenarios and images
    progress.onCompleted(...)
}
```

The app publishes its completion state before leaving the `use` block. At that point `ZipOutputStream.close()` has not run and the document provider has not necessarily finalized its metadata.

This ordering has existed since the original backup implementation in 2022 and is the leading app-side cause. It could allow DocumentsUI to observe and retain the initial zero-byte metadata created by `ACTION_CREATE_DOCUMENT`.

However, moving `progress.onCompleted` after stream closure still needs on-device validation. The current evidence does not rule out a separate DocumentsUI/DownloadsProvider refresh defect that may require an additional provider notification or different output-descriptor handling.

## Candidate fix and validation

1. Close/finalize the ZIP output stream before publishing `Backup.Completed`.
2. Add a log or query after closure to verify the exported document's `OpenableColumns.SIZE` is non-zero.
3. On-device test:
   - export a small scenario;
   - verify DocumentsUI immediately reports the real size;
   - send it through LocalSend;
   - confirm the Windows copy has the same size and SHA-256 hash.

