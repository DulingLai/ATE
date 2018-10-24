/*******************************************************************************
 * Copyright (c) 2012 Secure Software Engineering Group at EC SPRIDE.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors: Christian Fritz, Steven Arzt, Siegfried Rasthofer, Eric
 * Bodden, and others.
 ******************************************************************************/
package dulinglai.android.ate.data.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dulinglai.android.ate.data.soot.SootMethodAndClass;
import dulinglai.android.ate.utils.sootUtils.SootMethodRepresentationParser;
import soot.SootMethod;

/**
 * Class representing a single method in the Android SDK
 *
 * @author Steven Arzt, Siegfried Rasthofer, Daniel Magin, Joern Tillmanns
 */
public class AndroidMethod extends SootMethodAndClass {

    private Set<String> permissions;

    public AndroidMethod(String methodName, String returnType, String className) {
        super(methodName, className, returnType, new ArrayList<String>());
        this.permissions = null;
    }

    public AndroidMethod(String methodName, List<String> parameters, String returnType, String className) {
        super(methodName, className, returnType, parameters);
        this.permissions = null;
    }

    public AndroidMethod(String methodName, List<String> parameters, String returnType, String className,
                         Set<String> permissions) {
        super(methodName, className, returnType, parameters);
        this.permissions = permissions;
    }

    public AndroidMethod(SootMethod sm) {
        super(sm);
        this.permissions = null;
    }

    public AndroidMethod(SootMethodAndClass methodAndClass) {
        super(methodAndClass);
        this.permissions = null;
    }

    public Set<String> getPermissions() {
        return this.permissions == null ? Collections.<String>emptySet() : this.permissions;
    }

    public void addPermission(String permission) {
        if (this.permissions == null)
            this.permissions = new HashSet<>();
        this.permissions.add(permission);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSignature());
        if (permissions != null)
            for (String perm : permissions) {
                sb.append(" ");
                sb.append(perm);
            }
        return sb.toString();
    }

    public String getSignatureAndPermissions() {
        String s = getSignature();
        if (permissions != null)
            for (String perm : permissions)
                s += " " + perm;
        return s;
    }

    /***
     * Static method to create AndroidMethod from Soot method signature
     *
     * @param signature
     *            The Soot method signature
     * @return The new AndroidMethod object
     */
    public static AndroidMethod createFromSignature(String signature) {
        if (!signature.startsWith("<"))
            signature = "<" + signature;
        if (!signature.endsWith(">"))
            signature = signature + ">";

        SootMethodAndClass smac = SootMethodRepresentationParser.v().parseSootMethodString(signature);
        return new AndroidMethod(smac.getMethodName(), smac.getParameters(), smac.getReturnType(), smac.getClassName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((permissions == null) ? 0 : permissions.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AndroidMethod other = (AndroidMethod) obj;
        if (permissions == null) {
            return other.permissions == null;
        } else return permissions.equals(other.permissions);
    }

}
