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

## Provider-level diagnosis

The file picker returns legacy DownloadsProvider document URIs such as:

```text
content://com.android.providers.downloads.documents/document/24219
```

For `provider mode test.zip`, immediately after closing the ZIP:

- the physical file contained 2,789 bytes;
- MediaStore reported `_size=2789`;
- querying the exact picker URI from Klick'r returned `OpenableColumns.SIZE=0`.

Restarting DocumentsUI did not refresh the value. The same files still displayed as `0 B`, ruling out a temporary Files-app cursor cache.

## App-side attempts

1. Moved `Backup.Completed` until after `ZipOutputStream.close()`: no change.
2. Opened the document with explicit `rwt` mode and queried it after closure: no change; the provider still returned size zero.
3. Tried a standard `ContentResolver.update` on the granted document URI: rejected with `UnsupportedOperationException: Update not supported`.
4. Tried updating the underlying `content://downloads/all_downloads/<id>` row as a diagnostic: rejected because it requires the system-only `android.permission.ACCESS_ALL_DOWNLOADS` permission or a direct grant for that different URI.
5. Notified observers after writing: the picker URI still returned zero.

The unsuccessful completion-order commit and all diagnostic code were removed from PR #934 after testing.

## Conclusion

This is most likely an Android/Xiaomi DownloadsProvider or DocumentsUI integration bug, not a Klick'r ZIP-writing bug that the app can solve through supported APIs.

The same underlying file receives two metadata representations:

- MediaStore/file path: correct size and readable contents.
- Legacy DownloadsProvider document URI returned by the picker: size zero.

LocalSend behavior matches those two paths:

- Selecting through `ACTION_OPEN_DOCUMENT` trusts the zero-size picker URI and transfers an empty file.
- Sharing the file to LocalSend uses a different URI backed by correct metadata and transfers the complete ZIP.

Recommended workaround: share the backup to LocalSend from Files, or copy it over ADB, rather than selecting it inside LocalSend's file picker. Do not file this as an upstream Klick'r issue unless it reproduces on another device/provider implementation or a supported app-side workaround is found.
