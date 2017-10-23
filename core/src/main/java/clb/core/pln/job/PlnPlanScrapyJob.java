package clb.core.pln.job;

/**
 * Created by Detai on 2017/5/16.
 */

import com.hand.hap.job.AbstractJob;
//import org.python.util.PythonInterpreter;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class PlnPlanScrapyJob extends AbstractJob {

    private static Logger log = LoggerFactory.getLogger(clb.core.pln.job.PlnPlanScrapyJob.class);
    private int calculation;

    public PlnPlanScrapyJob() {
    }

//    public static void executeScrapy() {

//        PythonInterpreter interpreter = new PythonInterpreter();
//        interpreter.execfile("F:\\Project\\财联邦\\爬虫\\clb_scrapy\\clb_scrapy\\main.py");
//        PyFunction func = (PyFunction) interpreter.get("adder", PyFunction.class);
//
//        int a = 2010, b = 2;
//        PyObject pyobj = func.__call__(new PyInteger(a), new PyInteger(b));
//        System.out.println("anwser = " + pyobj.toString());

//    }


//    public static void main(String args[])
//    {
//        PythonInterpreter interpreter = new PythonInterpreter();
//        interpreter.execfile("F:\\Project\\财联邦\\爬虫\\clb_scrapy\\clb_scrapy\\main.py");
//    }


    public void safeExecute(JobExecutionContext context) throws Exception {
        JobKey jobKey = context.getJobDetail().getKey();
        log.info("---" + jobKey + " executing at " + new Date());

        try {

//            PythonInterpreter interpreter = new PythonInterpreter();
//            interpreter.execfile("F:\\Project\\财联邦\\爬虫\\clb_scrapy\\clb_scrapy\\main.py");


            Runtime run = Runtime.getRuntime();
            try {
                // run.exec("cmd /k shutdown -s -t 3600");
                Process process = run.exec("cmd.exe /k start D: && cd F:\\Project\\财联邦\\爬虫\\clb_scrapy\\clb_scrapy && python main.py");

                InputStream input = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String szline;
                while ((szline  = reader.readLine()) != null) {
                    System.out.println(szline);
                }
                reader.close();
                process.waitFor();
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }


            int zero = 0;
            this.calculation = 4815 / zero;
        } catch (Exception var4) {
            log.info("--- Error in job!");
            throw var4;
        }

        log.info("---" + jobKey + " completed at " + new Date());
    }

    public boolean isRefireImmediatelyWhenException() {
        return false;
    }
}