/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1997-1999
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Igor Bukanov, igor@fastmail.fm
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License Version 2 or later (the "GPL"), in which
 * case the provisions of the GPL are applicable instead of those above. If
 * you wish to allow use of your version of this file only under the terms of
 * the GPL and not to allow others to use your version of this file under the
 * MPL, indicate your decision by deleting the provisions above and replacing
 * them with the notice and other provisions required by the GPL. If you do
 * not delete the provisions above, a recipient may use your version of this
 * file under either the MPL or the GPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.mozilla.javascript;

import java.util.Hashtable;

/**
 * Cache of generated classes and data structures to access Java runtime
 * from JavaScript.
 *
 * @author Igor Bukanov
 *
 * @since Rhino 1.5 Release 5
 */
public class ClassCache
{
    private static final Object AKEY = new Object();

    Hashtable classTable = new Hashtable();

    Hashtable javaAdapterGeneratedClasses = new Hashtable();

    ScriptableObject scope;

    /**
     * Associate ClassCache object with the given top-level scope.
     * The ClassCache object can only be associated with the given scope once.
     *
     * @param topScope scope to associate this ClassCache object with.
     * @return true if no prevous ClassCache objects were embedded into
     *         the scope and this ClassCache were successfully associated
     *         or false otherwise.
     *
     * @see #get(Scriptable scope)
     */
    public boolean associate(ScriptableObject topScope)
    {
        if (topScope.getParentScope() != null) {
            // Can only associate cache with top level scope
            throw new IllegalArgumentException();
        }
        if(this == topScope.associateValue(AKEY, this)) {
            scope = topScope;
            return true;
        }
        return false;
    }

}
