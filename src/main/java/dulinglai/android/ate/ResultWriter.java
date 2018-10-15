package dulinglai.android.ate;

import dulinglai.android.ate.graphBuilder.ComponentTransitionGraph;
import dulinglai.android.ate.graphBuilder.TransitionEdge;
import dulinglai.android.ate.graphBuilder.componentNodes.ActivityNode;
import org.pmw.tinylog.Logger;
import soot.util.dot.DotGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ResultWriter {

    public static void appendStringToResultFile(String outputPath, String packageName,
                                                String fileName, String toAppend) throws IOException {
        FileWriter fw = new FileWriter(outputPath+"/"+fileName, true);
        BufferedWriter resultWriter = new BufferedWriter(fw);

        resultWriter.write(packageName + " : " + toAppend);
        resultWriter.newLine();
        resultWriter.close();
    }

    public static void writeActivityListToFile(List<ActivityNode> activityNodeList,
                                               String filename, String outputPath) {
        try {
            FileWriter fw = new FileWriter(outputPath + "/" + filename + "_activity.txt", true);
            BufferedWriter resultWriter = new BufferedWriter(fw);
            for (ActivityNode activityNode : activityNodeList) {
                resultWriter.write(activityNode.getName());
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
