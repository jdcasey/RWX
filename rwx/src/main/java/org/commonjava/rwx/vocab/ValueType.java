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
package org.commonjava.rwx.vocab;

import org.apache.commons.codec.binary.Base64;
import org.commonjava.rwx.error.CoercionException;
import org.commonjava.rwx.util.ValueCoercion;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.commonjava.rwx.vocab.XmlRpcConstants.DATETIME_FORMAT;

public enum ValueType
{
    STRUCT( new ValueCoercion( "STRUCT non-coercion" )
    {
        @Override
        public String toString( final Object value ) throws CoercionException
        {
            throw new CoercionException( "Cannot coerce STRUCT types." );
        }

        @Override
        public Object fromString( final String value ) throws CoercionException
        {
            throw new CoercionException( "Cannot coerce STRUCT types." );
        }
    }, Map.class, XmlRpcConstants.STRUCT ),

    ARRAY( new ValueCoercion( "ARRAY non-coercion" )
    {
        @Override
        public String toString( final Object value ) throws CoercionException
        {
            throw new CoercionException( "Cannot coerce ARRAY types." );
        }

        @Override
        public Object fromString( final String value ) throws CoercionException
        {
            throw new CoercionException( "Cannot coerce ARRAY types." );
        }
    }, List.class, XmlRpcConstants.ARRAY ),

    NIL( new ValueCoercion( "NIL-to-null" )
    {
        @Override
        public Object fromString( final String value ) throws CoercionException
        {
            return Nil.NIL_VALUE;
        }

    }, Nil.class, "nil" ),

    INT( new ValueCoercion( "INT-to-String" )
    {
        @Override
        public Object fromString( final String value ) throws CoercionException
        {
            try
            {
                String val = value == null ? null : value.trim();
                return val == null || val.length() < 1 ? null : Integer.parseInt( val );
            }
            catch ( final NumberFormatException e )
            {
                throw new CoercionException( e.getMessage(), e );
            }
        }
    }, Integer.class, "int", "i4" ),

    LONG( new ValueCoercion( "LONG-to-String" )
    {
        @Override
        public Object fromString( final String value ) throws CoercionException
        {
            try
            {
                String val = value == null ? null : value.trim();
                return val == null || val.length() < 1 ? null : Long.parseLong( val );
            }
            catch ( final NumberFormatException e )
            {
                throw new CoercionException( e.getMessage(), e );
            }
        }

        /**
         * If the value is of type Integer, convert it to Long
         */
        @Override
        public Object upgradeCast( Object value )
        {
            if ( value instanceof Long )
            {
                return value;
            }
            else if ( value instanceof Integer )
            {
                return new Long( (Integer) value );
            }
            throw new ClassCastException( "Can not cast " + value.getClass() + " to Long" );
        }
    }, Long.class, "i8" ),

    BOOLEAN( new ValueCoercion( "BOOLEAN-to-String" )
    {
        @Override
        public String toString( final Object value ) throws CoercionException
        {
            if ( value == null )
            {
                return null;
            }
            else
            {
                final String s = super.toString( value ).trim();
                if ( "1".equals( s ) || "0".equals( s ) )
                {
                    return s;
                }
                else
                {
                    return Boolean.valueOf( s ) ? "1" : "0";
                }
            }
        }

        @Override
        public Object fromString( final String value )
        {
            if ( value == null )
            {
                return null;
            }
            else
            {
                String val = value.trim();
                return "1".equals( val ) || Boolean.valueOf( val );
            }
        }
    }, Boolean.class, "boolean" ),

    STRING( new ValueCoercion( "STRING-to-String (with trim)" )
    {
        @Override
        public Object fromString( final String value )
        {
            return value == null ? null : value;
        }
    }, String.class, "string" ),

    DOUBLE( new ValueCoercion( "DOUBLE-to-String" )
    {
        @Override
        public Object fromString( final String value ) throws CoercionException
        {
            try
            {
                String val = value == null ? null : value.trim();
                return val == null || val.length() < 1 ? null : Double.parseDouble( val );
            }
            catch ( final NumberFormatException e )
            {
                throw new CoercionException( e.getMessage(), e );
            }
        }

        @Override
        public String toString( final Object value )
        {
            if ( value == null )
            {
                return null;
            }
            // Double.toString use scientist format, e.g., 2.135E9, which is not what we want
            DecimalFormat df = new DecimalFormat( "0", DecimalFormatSymbols.getInstance( Locale.ENGLISH ) );
            df.setMaximumFractionDigits( 340 ); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
            return df.format( ( (Number) value ).doubleValue() );
        }
    }, Number.class, "double" ),

    DATETIME( new ValueCoercion( "DATETIME-to-String (" + DATETIME_FORMAT + ")" )
    {
        @Override
        public Object fromString( String value ) throws CoercionException
        {
            if ( value == null )
            {
                return null;
            }
            value = value.trim();
            Date obj = null;
            ParseException exception = null;
            for ( String format : DATETIME_FORMAT )
            {
                try
                {
                    obj = new SimpleDateFormat( format ).parse( value );
                    break;
                }
                catch ( final ParseException e )
                {
                    exception = e;
                }
            }
            if ( obj != null )
            {
                return obj;
            }
            else
            {
                throw new CoercionException( "Cannot parse date: '" + value + "'.", exception );
            }
        }

        @Override
        public String toString( final Object value ) throws CoercionException
        {
            try
            {
                return value == null ? null : new SimpleDateFormat( DATETIME_FORMAT[0] ).format( (Date) value );
            }
            catch ( final ClassCastException e )
            {
                throw new CoercionException( "Not a java.util.Date.", e );
            }
        }

    }, Date.class, "dateTime.iso8601" ),

    BASE64( new ValueCoercion( "BASE64-to-String" )
    {
        @Override
        public Object fromString( final String value ) throws CoercionException
        {
            if ( value == null )
            {
                return null;
            }

            final byte[] result = Base64.decodeBase64( value.trim() );
            if ( result.length < 1 && value.length() > 0 && !value.equals( "==" ) && !value.equals( "=" ) )
            {
                throw new CoercionException( "Invalid Base64 input: " + value );
            }

            return result;
        }

        @Override
        public String toString( final Object value ) throws CoercionException
        {
            try
            {
                return value == null ?
                                null :
                                new String( Base64.encodeBase64( value instanceof String ?
                                                                                 ( (String) value ).getBytes() :
                                                                                 (byte[]) value ) );
            }
            catch ( final ClassCastException e )
            {
                throw new CoercionException( "Not a byte array.", e );
            }
        }

    }, byte[].class, "base64" ),;

    private String[] tags;

    private Class<?> nativeType;

    private ValueCoercion coercion;

    ValueType( final ValueCoercion coercion, final Class<?> nativeType, final String... tags )
    {
        this.coercion = coercion;
        this.nativeType = nativeType;
        this.tags = tags;
    }

    public ValueCoercion coercion()
    {
        return coercion;
    }

    public String getPrimaryTag()
    {
        return tags[0];
    }

    public static ValueType safeTypeFor( final Object value )
    {
        final ValueType type = typeFor( value );
        return type == null ? STRING : type;
    }

    public static ValueType typeFor( final Class clazz )
    {
        ValueType result = null;
        for ( final ValueType vt : values() )
        {
            if ( vt.nativeType.equals( clazz ) )
            {
                result = vt;
                break;
            }
        }
        return result;
    }

    public static ValueType typeFor( final Object value )
    {
        ValueType result = null;

        if ( value == null )
        {
            result = NIL;
        }
        else
        {
            final Class<?> cls = value instanceof Class<?> ? (Class<?>) value : value.getClass();
            for ( final ValueType vt : values() )
            {
                if ( vt.nativeType.isAssignableFrom( cls ) )
                {
                    result = vt;
                    break;
                }
            }
        }

        return result;
    }

    public static ValueType typeOf( final String tag )
    {
        if ( tag == null || tag.trim().length() < 1 )
        {
            return STRING;
        }

        for ( final ValueType type : values() )
        {
            for ( final String t : type.tags )
            {
                if ( t.equals( tag ) )
                {
                    return type;
                }
            }
        }

        return STRING;
    }

    public String toString()
    {
        return name();
    }

}
