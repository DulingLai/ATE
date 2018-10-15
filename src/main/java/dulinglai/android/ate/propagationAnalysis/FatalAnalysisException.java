package dulinglai.android.ate.propagationAnalysis;

/**
 * A fatal analysis exception. This should cause the analysis to stop but it can be caught at a
 * higher level to allow some processing before terminating (closing files, etc.).
 */
public class FatalAnalysisException extends Exception {
    private static final long serialVersionUID = -4890708762379522641L;

    public FatalAnalysisException() {
        super();
    }

    public FatalAnalysisException(String message) {
        super(message);
    }

    public FatalAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatalAnalysisException(Throwable cause) {
        super(cause);
    }
}
