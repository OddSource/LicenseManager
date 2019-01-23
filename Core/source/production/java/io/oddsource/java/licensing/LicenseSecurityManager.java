/*
 * LicenseSecurityManager.java from LicenseManager modified Monday, June 25, 2012 23:54:40 CDT (-0500).
 *
 * Copyright 2010-2013 the original author or authors.
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

package io.oddsource.java.licensing;

import java.io.FileDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.net.InetAddress;
import java.security.AccessControlException;
import java.security.BasicPermission;
import java.security.Permission;

import io.oddsource.java.licensing.exception.InsecureEnvironmentError;

/**
 * This security manager is one of the most integral pieces to the license manager. It prevents reflection attacks from
 * disabling or compromising the security features in this product.<br />
 * <br />
 * When the security manager is initialized, it first checks if a different security manager is already installed in
 * this JVM. If no security manager is installed already, then this security manager installs itself.<br />
 * <br />
 * If another security manager is already installed, this checks to make sure it prevents reflection attacks against
 * critical LicenseManager classes. If it prevents attacks, it is a suitable security manager and allowed to remain. If
 * it does not prevent attacks, this attempts to override the currently installed security manager and install itself.
 * If the existing security manager prevents this from installing itself, an {@link InsecureEnvironmentError} is
 * thrown and the LicenseManager fails to start.<br />
 * <br />
 * When this security manager installs itself over another, it will nest the other security manager within itself and
 * call all appropriate checking methods on that other security manager after this manager performs its analog
 * checks.<br />
 * <br />
 * When reflection is used to access non-public methods, fields, classes or interfaces, the JVM first consults the
 * installed security manager to ensure that the access is permitted. This security manager throws an exception if the
 * protected or private object being accessed via reflection belongs to the security manager package.<br />
 * <br />
 * Finally, this security manager will prevent other security managers from installing themselves over this one, so
 * that these security measures are not compromised.
 *
 * @author Nick Williams
 * @version 2.0.0
 * @since 1.0.0
 */
final class LicenseSecurityManager extends SecurityManager
{
    private static LicenseSecurityManager instance;

    private static final String FEATURE_RESTRICTION = FeatureRestriction.class.getCanonicalName();

    private static final String SET_SECURITY_MANAGER_PERMISSION_STRING = "setSecurityManager";

    private static final String SUPPRESS_ACCESS_CHECKS_PERMISSION_STRING = "suppressAccessChecks";

    private static final RuntimePermission SET_SECURITY_MANAGER_PERMISSION =
        new RuntimePermission(SET_SECURITY_MANAGER_PERMISSION_STRING);

    private static final short CHECK_PERMISSION_CALLER_STACK_INDEX = 5;

    private final SecurityManager next;

    static
    {
        final SecurityManager manager = System.getSecurityManager();
        if(manager == null)
        {
            // install the security manager
            LicenseSecurityManager.installSecurityManagerWithParent(null);
        }
        else if(!manager.getClass().equals(LicenseSecurityManager.class))
        {
            if(!LicenseSecurityManager.securityManagerIsSuitableReplacement(manager))
            {
                // if it's not a suitable replacement, reset the security manager
                LicenseSecurityManager.installSecurityManagerWithParent(manager);
            }
        }
    }

    private LicenseSecurityManager(final SecurityManager next)
    {
        super();

        this.next = next;
    }

    protected static LicenseSecurityManager getInstance()
    {
        return LicenseSecurityManager.instance;
    }

    static boolean securityManagerIsSuitableReplacement(final SecurityManager securityManager)
    {
        if(securityManager == null)
        {
            throw new IllegalArgumentException("Parameter securityManager cannot be null!");
        }

        // Make sure we can't call java.lang.reflect.AccessibleObject#setAccessible on License/LicenseManager methods
        // Make sure we can't call java.lang.System#setSecurityManager()
        try
        {
            securityManager.checkPermission(new ObjectReflectionPermission(
                LicenseSecurityManager.SUPPRESS_ACCESS_CHECKS_PERMISSION_STRING,
                new AccessibleObject[] {License.class.getDeclaredMethod("deserialize", byte[].class)}
            ));
            securityManager.checkPermission(new ObjectReflectionPermission(
                LicenseSecurityManager.SUPPRESS_ACCESS_CHECKS_PERMISSION_STRING,
                new AccessibleObject[] {LicenseManager.class.getMethod("validateLicense", License.class)}
            ));
            securityManager.checkPermission(LicenseSecurityManager.SET_SECURITY_MANAGER_PERMISSION);
            return false;
        }
        catch(final NoSuchMethodException e)
        {
            throw new AssertionError("Unexpected error", e);
        }
        catch(final SecurityException ignore)
        {
            // this is a good thing
        }

        return true;
    }

    private static void installSecurityManagerWithParent(final SecurityManager parent)
    {
        try
        {
            // install the security manager
            LicenseSecurityManager.instance = new LicenseSecurityManager(parent);
            System.setSecurityManager(LicenseSecurityManager.instance);
        }
        catch(final SecurityException e)
        {
            // since we can't install the security manager, indicate that the environment is insecure
            throw new InsecureEnvironmentError(e);
        }
    }

    @Override
    @Deprecated
    public void checkMemberAccess(final Class<?> reflectionClass, final int memberAccessType)
    {
        if(this.next != null)
        {
            this.next.checkMemberAccess(reflectionClass, memberAccessType);
        }
    }

    @Override
    public void checkPermission(final Permission permission)
    {
        if(permission.getName().equals(LicenseSecurityManager.SET_SECURITY_MANAGER_PERMISSION_STRING))
        {
            throw new SecurityException("Setting a SecurityManager other than the LicenseSecurityManager is " +
                                        "prohibited.");
        }

        if(permission.getName().equals(LicenseSecurityManager.SUPPRESS_ACCESS_CHECKS_PERMISSION_STRING))
        {
            if(permission instanceof LicenseSecurityManager.ObjectReflectionPermission)
            {
                this.checkObjectReflectPermission((LicenseSecurityManager.ObjectReflectionPermission) permission);
            }
            /*else
            {
                TODO Integrate ByteBuddy to be able to uncomment this (OddSource/java-license-manager#5)
                TODO https://stackoverflow.com/questions/2315066/is-there-a-way-for-a-securitymanager
                // throw new DefaultReflectionAccessControlException(
                //     "All member access elevation prohibited.",
                //     permission
                // );
            }*/
        }

        if(this.next != null)
        {
            this.next.checkPermission(permission);
        }
    }

    @Override
    public void checkPermission(final Permission permission, final Object object)
    {
        if(this.next != null)
        {
            this.next.checkPermission(permission, object);
        }
    }

    private void checkObjectReflectPermission(final LicenseSecurityManager.ObjectReflectionPermission permission)
    {
        for(final AccessibleObject target : permission.targets)
        {
            if(!(target instanceof Member))
            {
                continue;
            }

            final Class<?> targetClass = ((Member) target).getDeclaringClass();
            if(targetClass != null)
            {
                if(
                    targetClass == Class.class || targetClass == String.class || targetClass == System.class ||
                    AccessibleObject.class.isAssignableFrom(targetClass)
                )
                {
                    final Class<?>[] stack = getClassContext();
                    if(
                        stack.length < (LicenseSecurityManager.CHECK_PERMISSION_CALLER_STACK_INDEX - 1) ||
                        !stack[LicenseSecurityManager.CHECK_PERMISSION_CALLER_STACK_INDEX].getPackage().
                            getName().startsWith("java.")
                    )
                    {
                        throw new AccessControlException(
                            "Reflection access to non-public members of java.lang.Class, java.lang.String, " +
                            "java.lang.System, and java.lang.reflect.AccessibleObject prohibited."
                        );
                    }
                }

                final Package targetPackage = targetClass.getPackage();
                if(
                    targetPackage != null &&
                    targetPackage.getName().startsWith("io.oddsource.java.licensing") &&
                    !targetClass.getCanonicalName().equals(LicenseSecurityManager.FEATURE_RESTRICTION)
                )
                {
                    throw new AccessControlException(
                        "Reflection access to non-public members of LicenseManager class [" +
                        targetClass.getSimpleName() + "] prohibited.",
                        permission
                    );
                }
            }
        }
    }

    @Override
    public void checkPackageAccess(final String packageName)
    {
        if(this.next != null)
        {
            this.next.checkPackageAccess(packageName);
        }
    }

    @Override
    public void checkCreateClassLoader()
    {
        if(this.next != null)
        {
            this.next.checkCreateClassLoader();
        }
    }

    @Override
    public void checkAccess(final Thread thread)
    {
        if(this.next != null)
        {
            this.next.checkAccess(thread);
        }
    }

    @Override
    public void checkAccess(final ThreadGroup threadGroup)
    {
        if(this.next != null)
        {
            this.next.checkAccess(threadGroup);
        }
    }

    @Override
    public void checkExit(final int i)
    {
        if(this.next != null)
        {
            this.next.checkExit(i);
        }
    }

    @Override
    public void checkExec(final String s)
    {
        if(this.next != null)
        {
            this.next.checkExec(s);
        }
    }

    @Override
    public void checkLink(final String s)
    {
        if(this.next != null)
        {
            this.next.checkLink(s);
        }
    }

    @Override
    public void checkRead(final FileDescriptor fileDescriptor)
    {
        if(this.next != null)
        {
            this.next.checkRead(fileDescriptor);
        }
    }

    @Override
    public void checkRead(final String s)
    {
        if(this.next != null)
        {
            this.next.checkRead(s);
        }
    }

    @Override
    public void checkRead(final String s, final Object o)
    {
        if(this.next != null)
        {
            this.next.checkRead(s);
        }
    }

    @Override
    public void checkWrite(final FileDescriptor fileDescriptor)
    {
        if(this.next != null)
        {
            this.next.checkWrite(fileDescriptor);
        }
    }

    @Override
    public void checkWrite(final String s)
    {
        if(this.next != null)
        {
            this.next.checkWrite(s);
        }
    }

    @Override
    public void checkDelete(final String s)
    {
        if(this.next != null)
        {
            this.next.checkDelete(s);
        }
    }

    @Override
    public void checkConnect(final String s, final int i)
    {
        if(this.next != null)
        {
            this.next.checkConnect(s, i);
        }
    }

    @Override
    public void checkConnect(final String s, final int i, final Object o)
    {
        if(this.next != null)
        {
            this.next.checkConnect(s, i, o);
        }
    }

    @Override
    public void checkListen(final int i)
    {
        if(this.next != null)
        {
            this.next.checkListen(i);
        }
    }

    @Override
    public void checkAccept(final String s, final int i)
    {
        if(this.next != null)
        {
            this.next.checkAccept(s, i);
        }
    }

    @Override
    public void checkMulticast(final InetAddress inetAddress)
    {
        if(this.next != null)
        {
            this.next.checkMulticast(inetAddress);
        }
    }

    @Override
    @Deprecated
    public void checkMulticast(final InetAddress inetAddress, final byte b)
    {
        if(this.next != null)
        {
            this.next.checkMulticast(inetAddress, b);
        }
    }

    @Override
    public void checkPropertiesAccess()
    {
        if(this.next != null)
        {
            this.next.checkPropertiesAccess();
        }
    }

    @Override
    public void checkPropertyAccess(final String s)
    {
        if(this.next != null)
        {
            this.next.checkPropertyAccess(s);
        }
    }

    @Override
    public void checkPrintJobAccess()
    {
        if(this.next != null)
        {
            this.next.checkPrintJobAccess();
        }
    }

    @Override
    @Deprecated
    public void checkSystemClipboardAccess()
    {
        if(this.next != null)
        {
            this.next.checkSystemClipboardAccess();
        }
    }

    @Override
    @Deprecated
    public void checkAwtEventQueueAccess()
    {
        if(this.next != null)
        {
            this.next.checkAwtEventQueueAccess();
        }
    }

    @Override
    public void checkPackageDefinition(final String s)
    {
        if(this.next != null)
        {
            this.next.checkPackageDefinition(s);
        }
    }

    @Override
    public void checkSetFactory()
    {
        if(this.next != null)
        {
            this.next.checkSetFactory();
        }
    }

    @Override
    public void checkSecurityAccess(final String s)
    {
        if(this.next != null)
        {
            this.next.checkSecurityAccess(s);
        }
    }

    @Override
    @Deprecated
    public boolean checkTopLevelWindow(final Object window)
    {
        return this.next == null || this.next.checkTopLevelWindow(window);
    }

    @Override
    public ThreadGroup getThreadGroup()
    {
        return this.next != null ? this.next.getThreadGroup() : super.getThreadGroup();
    }

    @Override
    public Object getSecurityContext()
    {
        return this.next != null ? this.next.getSecurityContext() : super.getSecurityContext();
    }

    static final class ObjectReflectionPermission extends BasicPermission
    {
        private static final long serialVersionUID = 8319947110221501285L;

        transient final AccessibleObject[] targets;

        ObjectReflectionPermission(final String name, final AccessibleObject[] targets)
        {
            super(name);
            this.targets = targets;
        }
    }

    @SuppressWarnings("unused")
    static final class DefaultReflectionAccessControlException extends AccessControlException
    {
        private static final long serialVersionUID = 8137740110221501582L;

        DefaultReflectionAccessControlException(final String s, final Permission p)
        {
            super(s, p);
        }
    }
}
