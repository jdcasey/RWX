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
package org.commonjava.rwx.binding.testutil;

import static org.commonjava.rwx.binding.testutil.recipe.RecipeEventUtils.endParameter;

import org.commonjava.rwx.binding.anno.Contains;
import org.commonjava.rwx.binding.anno.DataIndex;
import org.commonjava.rwx.binding.anno.Response;
import org.commonjava.rwx.binding.mapping.ArrayMapping;
import org.commonjava.rwx.binding.mapping.FieldBinding;
import org.commonjava.rwx.binding.mapping.Mapping;
import org.commonjava.rwx.estream.model.Event;
import org.commonjava.rwx.estream.model.ParameterEvent;
import org.commonjava.rwx.estream.model.ResponseEvent;
import org.commonjava.rwx.estream.model.StructEvent;
import org.commonjava.rwx.estream.model.ValueEvent;
import org.commonjava.rwx.vocab.EventType;
import org.commonjava.rwx.vocab.ValueType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Response
public class SimpleAddressMapResponse
    implements TestObject
{

    @DataIndex( 0 )
    @Contains( SimpleAddress.class )
    private Map<String, SimpleAddress> addresses = Collections.singletonMap( "work", new SimpleAddress() );

    public Map<String, SimpleAddress> getAddresses()
    {
        return addresses;
    }

    public void setAddresses( final Map<String, SimpleAddress> addresses )
    {
        this.addresses = addresses;
    }

    public Map<Class<?>, Mapping<?>> recipes()
    {
        final Map<Class<?>, Mapping<?>> recipes = new HashMap<Class<?>, Mapping<?>>();

        final ArrayMapping recipe = new ArrayMapping( SimpleAddressMapResponse.class, new Integer[0] );

        recipe.addFieldBinding( 0, new FieldBinding( "addresses", Map.class, SimpleAddressMapResponse.class ) );

        recipes.put( SimpleAddressMapResponse.class, recipe );

        recipes.putAll( new SimpleAddress().recipes() );

        return recipes;

    }

    public List<Event<?>> events()
    {
        final List<Event<?>> check = new ArrayList<Event<?>>( Collections.singleton( new ResponseEvent( true ) ) );
        check.addAll( Arrays.asList( new ParameterEvent( 0 ), new StructEvent( EventType.START_STRUCT ) ) );

        for ( final Map.Entry<String, SimpleAddress> entry : getAddresses().entrySet() )
        {
            check.add( new StructEvent( entry.getKey() ) );

            check.addAll( entry.getValue().events() );

            check.addAll( Arrays.asList( new ValueEvent( entry.getValue(), ValueType.STRUCT ),
                                         new StructEvent( entry.getKey(), entry.getValue(), ValueType.STRUCT ),
                                         new StructEvent( EventType.END_STRUCT_MEMBER ) ) );

        }

        check.add( new StructEvent( EventType.END_STRUCT ) );
        check.addAll( endParameter( 0, getAddresses(), ValueType.STRUCT ) );
        check.add( new ResponseEvent( false ) );

        return check;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( addresses == null ) ? 0 : addresses.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final SimpleAddressMapResponse other = (SimpleAddressMapResponse) obj;
        if ( addresses == null )
        {
            if ( other.addresses != null )
            {
                return false;
            }
        }
        else if ( !addresses.equals( other.addresses ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "SimpleAddressMapResponse{" + "addresses=" + addresses + '}';
    }
}
