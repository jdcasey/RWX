/*
 *  Copyright (C) 2010 John Casey.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.commonjava.rwx.apps.jira;

import org.commonjava.rwx.binding.anno.DataIndex;
import org.commonjava.rwx.binding.anno.IndexRefs;
import org.commonjava.rwx.binding.anno.Request;

@Request( method = "jira1.getServerInfo" )
public class ServerInfoRequest
{

    public static final String VERSION = "version";

    @DataIndex( 0 )
    private final String infoType;

    @IndexRefs( 0 )
    public ServerInfoRequest( final String infoType )
    {
        this.infoType = infoType;
    }

    @IndexRefs( 0 )
    public ServerInfoRequest()
    {
        infoType = null;
    }

    public String getInfoType()
    {
        return infoType;
    }

}
