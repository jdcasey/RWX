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
package org.commonjava.rwx.impl.stax.helper;

import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.impl.TrackingXmlRpcListener;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.vocab.ValueType;
import org.commonjava.rwx.vocab.XmlRpcConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class ParamHelper
    implements XMLStreamConstants
{

    public static void parse( final XMLStreamReader reader, final XmlRpcListener listener )
        throws XMLStreamException, XmlRpcException
    {
        int count = 0;
        int type = -1;
        do
        {
            type = reader.nextTag();
            if ( type == START_ELEMENT )
            {
                if ( XmlRpcConstants.VALUE.equals( reader.getName().getLocalPart() ) )
                {
                    Logger logger = LoggerFactory.getLogger( ParamHelper.class );
                    logger.trace( "Starting parameter: {}", count );
                    listener.startParameter( count );

                    final ValueHelper vh = new ValueHelper();
                    vh.parse( reader, listener );

                    final Object value = vh.getValue();
                    final ValueType vt = vh.getValueType();

                    listener.parameter( count, value, vt );
                    listener.endParameter();
//                    listener.value( value, vt );
                    logger.trace( "Finished parameter: {}", count );

                    count++;
                }
            }
            else if ( type == XMLStreamReader.END_ELEMENT
                && XmlRpcConstants.PARAMS.equals( reader.getName().getLocalPart() ) )
            {
                break;
            }
        }
        while ( type != END_DOCUMENT );
    }

}
