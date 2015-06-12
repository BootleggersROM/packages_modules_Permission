/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.packageinstaller.permission.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;

import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.PermissionApps.PermissionApp;

public class Utils {

    private static final String LOG_TAG = "Utils";

    public static final String OS_PKG = "android";

    public static final String[] MODERN_PERMISSION_GROUPS = {
            Manifest.permission_group.CALENDAR,
            Manifest.permission_group.CAMERA,
            Manifest.permission_group.CONTACTS,
            Manifest.permission_group.LOCATION,
            Manifest.permission_group.SENSORS,
            Manifest.permission_group.SMS,
            Manifest.permission_group.PHONE,
            Manifest.permission_group.MICROPHONE,
            Manifest.permission_group.STORAGE
    };

    private Utils() {
        /* do nothing - hide constructor */
    }

    public static Drawable loadDrawable(PackageManager pm, String pkg, int resId) {
        try {
            return pm.getResourcesForApplication(pkg).getDrawable(resId, null);
        } catch (Resources.NotFoundException | PackageManager.NameNotFoundException e) {
            Log.d(LOG_TAG, "Couldn't get resource", e);
            return null;
        }
    }

    public static boolean isModernPermissionGroup(String name) {
        for (String modernGroup : MODERN_PERMISSION_GROUPS) {
            if (modernGroup.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldShowPermission(AppPermissionGroup group, boolean showLegacy) {
        // We currently will not show permissions fixed by the system.
        // which is what the system does for system components.
        if (group.isSystemFixed()) {
            return false;
        }

        // Yes this is possible. We have leftover permissions that
        // are not in the final groups and we want to get rid of,
        // therefore we do not have app ops for legacy support.
        if (!group.hasRuntimePermission() && !group.hasAppOpPermission()) {
            return false;
        }

        final boolean isPlatformPermission = group.getDeclaringPackage().equals(OS_PKG);
        // Show legacy permissions only if the user chose that.
        if (isPlatformPermission && !showLegacy
                && !Utils.isModernPermissionGroup(group.getName())) {
            return false;
        }
        return true;
    }

    public static boolean shouldShowPermission(PermissionApp app) {
        // We currently will not show permissions fixed by the system
        // which is what the system does for system components.
        if (app.isSystemFixed()) {
            return false;
        }

        // Yes this is possible. We have leftover permissions that
        // are not in the final groups and we want to get rid of,
        // therefore we do not have app ops for legacy support.
        if (!app.hasRuntimePermissions() && !app.hasAppOpPermissions()) {
            return false;
        }
        return true;
    }

    public static Drawable applyTint(Context context, Drawable icon, int attr) {
        Theme theme = context.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attr, typedValue, true);
        icon.clearColorFilter();
        icon.setTint(context.getColor(typedValue.resourceId));
        return icon;
    }
}
