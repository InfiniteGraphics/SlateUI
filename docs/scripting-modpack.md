# Scripting and Modpack Authoring

KubeJS and CraftTweaker support starts as an integration investigation. `ScriptingIntegrationReport` records the target, status, and notes without adding a hard dependency.

`DataDrivenScreenRegistration` describes screens provided by data or resource packs. `ServerProvidedUiPolicy` keeps server-provided UI schemas disabled by default unless a mod explicitly opts in.

`ExternalUiSecurityPolicy` restricts command namespaces, bindings, and resource overrides. `SandboxedCommandModel` carries external command requests. `PackValidationCli` is the CLI entry point for pack validation.
