/**
 * Copyright (C) 2010 Red Hat, Inc. (jdcasey@commonjava.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.rwx.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ruhan on 7/19/17.
 */
public class Registry
{
    protected Map<Class, Parser> parserMap = new ConcurrentHashMap<>();
    protected Map<Class, Renderer> rendererMap = new ConcurrentHashMap<>();

    protected void setParser(Class cls, Parser parser)
    {
        parserMap.put( cls, parser );
    }

    protected void setRenderer(Class cls, Renderer renderer)
    {
        rendererMap.put( cls, renderer );
    }

    public <T> T parseAs( Object o, Class<T> type )
    {
        Parser parser = parserMap.get( type );
        if ( parser == null )
        {
            throw new RuntimeException( "Parser not found for " + type.getName() );
        }
        return type.cast( parser.parse( o ) );
    }

    public Object renderTo( Object obj )
    {
        Renderer renderer = rendererMap.get( obj.getClass() );
        if ( renderer == null )
        {
            throw new RuntimeException( "Renderer not found for " + obj.getClass() );
        }
        return renderer.render( obj );
    }

    public boolean hasRender( Class type )
    {
        return rendererMap.get( type ) != null;
    }

    public boolean hasParser( Class type )
    {
        return parserMap.get( type ) != null;
    }

    // singleton instance

    private static Registry instance = new Registry(); // default

    public static synchronized Registry getInstance() {
        return instance;
    }

    public static synchronized void setInstance( Registry registry ) {
        instance = registry;
    }
}
