# Notice

This project bundles the **NewBlackbox** virtualization engine (modules `Bcore`, `black-reflection`, `compiler`),
licensed under Apache License 2.0 — see `ENGINE-LICENSE-Apache-2.0.txt`.
Source: https://github.com/ALEX5402/NewBlackbox

Change vs upstream default: the engine's crash-log uploader (sends logcat to a third-party server)
is disabled in `ParallelApp.java` via `getLogSenderChatId()` returning an empty string.
