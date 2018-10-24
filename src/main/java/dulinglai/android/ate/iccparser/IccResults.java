package dulinglai.android.ate.iccparser;

import java.util.HashSet;
import java.util.Set;

public class IccResults {

    // TODO Remove this method if we are not using it
//	public static InfoflowResults clean(IInfoflowCFG cfg, InfoflowResults results) {
//		if (null == results) {
//			return results;
//		}
//
//		InfoflowResults cleanResults = new InfoflowResults();
//
//		Set<String> iccSources = new HashSet<String>();
//		Set<String> iccSinks = new HashSet<String>();
//
//		if (!results.isEmpty()) {
//			for (ResultSinkInfo sink : results.getResults().keySet()) {
//				for (ResultSourceInfo source : results.getResults().get(sink)) {
//					String sourceBelongingClass = cfg.getMethodOf(source.getStmt()).getDeclaringClass().getName();
//					String sinkBelongingClass = cfg.getMethodOf(sink.getStmt()).getDeclaringClass().getName();
//
//					if (!sourceBelongingClass.equals(sinkBelongingClass)) {
//						String iccSource = cfg.getMethodOf(source.getStmt()).getSignature() + "/" + source.getStmt();
//						iccSources.add(iccSource);
//
//						String iccSink = cfg.getMethodOf(sink.getStmt()).getSignature() + "/" + sink.getStmt();
//						iccSinks.add(iccSink);
//
//						cleanResults.addResult(sink, source);
//					}
//				}
//			}
//		}
//
//		return cleanResults;
//	}

}
