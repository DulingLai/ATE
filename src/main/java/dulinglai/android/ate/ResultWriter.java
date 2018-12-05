package dulinglai.android.ate;

import dulinglai.android.ate.model.ComponentTransitionGraph;
import dulinglai.android.ate.model.TransitionEdge;
import dulinglai.android.ate.model.components.Activity;
import org.pmw.tinylog.Logger;
import soot.util.dot.DotGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ResultWriter {

    private static final String RESULT_WRITER = "ResultWriter";

    private static FileWriter logFileWriter;

    public static void initialize(String logDirString, String apkNameString) {
        logFileWriter = createLogDirIfNotExist(logDirString, apkNameString);
    }

    /**
     * Creates the logging directory if it does not exist
     * @param logDir The logging direct
     * @param apkName The name of the apk
     */
    private static FileWriter createLogDirIfNotExist(String logDir, String apkName) {
        File dir = new File(logDir);
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (success)
                Logger.info("[{}] Successfully created the logging directory!");
            else
                Logger.warn("[WARN] Failed to create the loggin directory!");
        }

        File file = new File(dir + File.separator + apkName + "_log.txt");
        try{
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, true);
            return fileWriter;
        } catch (IOException e) {
            Logger.error("[ERROR] Failed to create new log file: {}", e.getMessage());
            System.exit(-1);
            return null;
        }
    }

    /**
     * Append the string to the end of the file
     * @param message The string to append to the file
     */
    public static void writeToFile(String message) {
        BufferedWriter bufferedWriter = new BufferedWriter(logFileWriter);
        try {
            bufferedWriter.append(message).append("\n");
            bufferedWriter.close();
        } catch (IOException e) {
            Logger.error("[ERROR] Failed to write to log file: {}", e.getMessage());
        }
    }

    public static void appendStringToResultFile(String outputPath, String packageName,
                                                String fileName, String toAppend) throws IOException {
        FileWriter fw = new FileWriter(outputPath+"/"+fileName, true);
        BufferedWriter resultWriter = new BufferedWriter(fw);

        resultWriter.write(packageName + " : " + toAppend);
        resultWriter.newLine();
        resultWriter.close();
    }

    public static void writeActivityListToFile(Set<Activity> activityList,
                                               String filename, String outputPath) {
        try {
            FileWriter fw = new FileWriter(outputPath + "/" + filename + "_activity.txt", true);
            BufferedWriter resultWriter = new BufferedWriter(fw);
            for (Activity activity : activityList) {
                resultWriter.write(activity.getName());
                resultWriter.newLine();
            }
            resultWriter.close();
        } catch (IOException e) {
            Logger.error("Cannot write to file: {}", e.getMessage());
        }
    }

    public static void writeComponentTransitionGraph(ComponentTransitionGraph ctg, String outputPath, String packageName) {
        Iterator<TransitionEdge> edgeIterator = ctg.getTransitionEdges().iterator();
        DotGraph dotGraph = new DotGraph("ComponentTransitionGraph");
        while (edgeIterator.hasNext()) {
            TransitionEdge edge = edgeIterator.next();
            String src = edge.getSrcComp().getName();
            String tgt = edge.getTgtComp().getName();

            // widgets
            String widget = "";
            if (edge.getWidget()!=null) {
                String widget_id;
                String widget_text;
                widget_id= String.valueOf(edge.getWidget().getResourceId());
                widget_text = edge.getWidget().getText();
                if (widget_text!=null && !widget_text.isEmpty())
                    widget = "Widget(id: " + edge.getWidget().getResourceId() + " | text: " + edge.getWidget().getText() + ")";
                else if (widget_id!=null && !widget_id.isEmpty())
                    widget = "Widget(id: " + edge.getWidget().getResourceId() + ")";
            }

            dotGraph.drawEdge(src, tgt).setLabel(widget);
        }

        dotGraph.plot(outputPath + File.separator + packageName + ".dot");
    }
}
