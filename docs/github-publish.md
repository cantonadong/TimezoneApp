# GitHub publishing on this machine

The sandbox cannot write the default GitHub CLI configuration, global Git
configuration, or the repository's original `.git` directory. This project
therefore keeps persistent local state under `.local/`, which Git ignores:

- `.local/gh/`: GitHub CLI login and configuration
- `.local/gitconfig`: Git credential helper configured by `gh auth setup-git`
- `.local/git/`: writable Git metadata for this worktree's `main` branch

The login is local to this machine and is reused by the publishing script. Do
not delete `.local/` unless you intend to recreate the Git metadata and sign
in again. No token or signing key is committed to the repository.

After building and committing a new version on `main`, publish with:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\publish-release.ps1 -Tag vNEXT -Apk .\dist\TimezoneApp.apk -Notes .\docs\release-vNEXT.md
```

The script loads the saved GitHub login, pushes `main`, and creates a Release
with the APK. To run `gh` manually in a PowerShell session, first run:

```powershell
powershell -ExecutionPolicy Bypass -NoExit -Command ". .\scripts\github-env.ps1"
gh auth status
```

Use `powershell -ExecutionPolicy Bypass` for scripts if the default execution
policy prevents running local `.ps1` files.
