/*
 * Performance Control - An Android CPU Control application Copyright (C) 2012
 * James Roberts
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.androguide.honamicontrol.helpers.CMDProcessor;

import android.util.Log;

public final class CMDProcessor {
    private static final String TAG = "CMDProcessor";

    public CMDProcessor() {

    }

    /* Run a system command with full redirection */
    public static ChildProcess startSysCmd(String[] cmdarray, String childStdin) {
        return new ChildProcess(cmdarray, childStdin);
    }

    public static CommandResult runSysCmd(String[] cmdarray, String childStdin) {
        ChildProcess proc = startSysCmd(cmdarray, childStdin);
        proc.waitFinished();
        return proc.getResult();
    }

    public static ChildProcess startShellCommand(String cmd) {
        String[] cmdarray = new String[3];
        cmdarray[0] = "sh";
        cmdarray[1] = "-c";
        cmdarray[2] = cmd;
        return startSysCmd(cmdarray, null);
    }

    public static CommandResult runShellCommand(String cmd) {
        ChildProcess proc = startShellCommand(cmd);
        proc.waitFinished();
        return proc.getResult();
    }

    public static ChildProcess startSuCommand(String cmd) {
        String[] cmdarray = new String[3];
        cmdarray[0] = "su";
        cmdarray[1] = "-c";
        cmdarray[2] = cmd;
        return startSysCmd(cmdarray, null);
    }

    public static CommandResult runSuCommand(String cmd) {
        ChildProcess proc = startSuCommand(cmd);
        proc.waitFinished();
        return proc.getResult();
    }

    public static boolean canSU() {
        CommandResult r = runShellCommand("id");
        StringBuilder out = new StringBuilder(0);
        out.append(r.getStdout());
        out.append(" ; ");
        out.append(r.getStderr());
        Log.d(TAG, "canSU() su[" + r.getExitValue() + "]: " + out);
        return r.success();
    }
}
