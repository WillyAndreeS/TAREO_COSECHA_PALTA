package com.acpagro.tareopalta.modelo;

import android.content.Context;

public class APKInstall {
    public static APKBuilder with(Context context) {
        return new APKBuilder(context);
    }

    public static class APKBuilder {
        private Context context;
        private String apkPath;
        private String uri;
        private boolean forceInstall;

        public APKBuilder(Context context) {
            this.context = context;
        }

        public APKBuilder from(String apkPath) {
            this.apkPath = apkPath;
            return this;
        }

        public APKBuilder fromUri(String apkUri) {
            this.uri = apkUri;
            return this;
        }

        public void forceInstall() {
            this.forceInstall = true;
            install();
        }

        public void install() {
            APKRequestInstallPermissionActivity.start(context, apkPath, uri, forceInstall);
        }
    }
}
