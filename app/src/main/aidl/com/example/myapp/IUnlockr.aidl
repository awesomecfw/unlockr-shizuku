package com.example.myapp;

interface IUnlockr {
    String getVersion();
    int getServerUid();
    boolean isPrivileged();
    String exec(String command);
    String readFile(String path);
    boolean writeFile(String path, String value);
    boolean hasAccess(String packageName);
}
