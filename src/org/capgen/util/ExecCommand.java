package org.capgen.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class ExecCommand {
    public String execOneThread(String command, String workingpath) {
        final StringBuffer result = new StringBuffer("");
        try {
            File dir = new File(workingpath);
            Process process = Runtime.getRuntime().exec(command, null, dir);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            while ((line = stdInput.readLine()) != null) {
                result.append(line + "\n");
            }

            while ((line = stdError.readLine()) != null) {
                System.out.println(line);
            }

            stdInput.close();
            stdError.close();
        } catch (Exception e) {
            System.err.println("Error:" + command);
            return null;
        }
        return result.toString();
    }

    public Pair<String,String> execOneThread(String[] commands, String workingpath) {
        ReadStream s1,s2;
        Pair<String,String> result = null;
        try {
            File dir = new File(workingpath);
//			System.out.println("Running Commands");
//			System.out.println(dir);
            Process process = Runtime.getRuntime().exec(commands, null, dir);

            s1 = new ReadStream("stdin",process.getInputStream());
            s2 = new ReadStream("stderr", process.getErrorStream());
            s1.start();
            s2.start();
            if (!process.waitFor(2, TimeUnit.MINUTES)) {
                result = new Pair<String, String>("Timeout", "Timeout");
                s1.end();
                s2.end();
                process.destroy();
            }
            else result = new Pair<String,String>(s1.output,s2.output);
//			String line = null;
//			while ((line = stdInput.readLine()) != null) {
//				System.out.println(line);
//				result.append(line + "\n");
//			}
//
//			while ((line = stdError.readLine()) != null) {
//				System.err.println(line);ls
//				errors.append(line + "\n");
//			}
//
//			stdInput.close();
//			stdError.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error:" + e.getClass());
            return result;
        }
        return result;
    }
}
