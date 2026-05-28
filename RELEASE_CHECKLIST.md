# Release Checklist

Use this checklist before cutting a tag.

1. Run `.\gradlew.bat test`.
2. Confirm `gradle.properties` contains the intended `version`, `group`, `mod_id`, and `license`.
3. Confirm resource warnings are understood and are not Java compile failures.
4. Review `README.md`, `ROADMAP.md`, `ARCHITECTURE.md`, and `MIGRATION.md`.
5. Update `CHANGELOG.md`.
6. Create an annotated tag with the release number.
7. Push the branch and tags.

PowerShell:

```powershell
$env:JAVA_HOME = "D:\ENV\jdk\temurin-21.0.11"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat test
git tag -a 0.3 -m "Release 0.3"
git push origin main --tags
```
