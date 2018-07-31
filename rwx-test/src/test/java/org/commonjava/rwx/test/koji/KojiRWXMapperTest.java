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
package org.commonjava.rwx.test.koji;

import org.commonjava.rwx.api.RWXMapper;
import org.commonjava.rwx.test.AbstractTest;
import org.commonjava.rwx.test.simple.RequestWithOneParam;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by ruhan on 8/2/17.
 */
public class KojiRWXMapperTest
                extends AbstractTest
{

    @Test
    public void roundTrip_KrbLoginRequest() throws Exception
    {
        String source = getXMLString( "kojiKrbLoginRequest" );
        KrbLoginRequest parsed = new RWXMapper().parse( new ByteArrayInputStream( source.getBytes() ),
                                                        KrbLoginRequest.class );

        KrbLoginRequest expected = new KrbLoginRequest();
        expected.setKrbRequest( "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZy4KVGhlIHF1aWNrIGJy\n"
                                                + "aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZy4=\n" );

        assertEquals( expected.getKrbRequest(), parsed.getKrbRequest() );

        KrbLoginRequest rounded =
                        new RWXMapper().parse( new ByteArrayInputStream( new RWXMapper().render( parsed ).getBytes() ),
                                               KrbLoginRequest.class );
        assertEquals( expected.getKrbRequest(), rounded.getKrbRequest() );
    }

    @Test
    public void roundTrip_GetBuildByNVRObjRequest() throws Exception
    {
        String source = getXMLString( "kojiGetBuildByNVRObjRequest" );
        GetBuildByNVRObjRequest parsed = new RWXMapper().parse( new ByteArrayInputStream( source.getBytes() ),
                                                                GetBuildByNVRObjRequest.class );

        GetBuildByNVRObjRequest expected = new GetBuildByNVRObjRequest(
                        new KojiNVR( "org.dashbuilder-dashbuilder-parent-metadata", "0.4.0.Final", "1" ) );

        assertEquals( expected.getNvr().renderString(), parsed.getNvr().renderString() );

        GetBuildByNVRObjRequest rounded =
                        new RWXMapper().parse( new ByteArrayInputStream( new RWXMapper().render( parsed ).getBytes() ),
                                               GetBuildByNVRObjRequest.class );
        assertEquals( expected.getNvr().renderString(), rounded.getNvr().renderString() );
    }

    @Test
    public void roundTrip_GetBuildRequest() throws Exception
    {
        String source = getXMLString( "kojiGetBuildRequest" );
        GetBuildRequest parsed =
                        new RWXMapper().parse( new ByteArrayInputStream( source.getBytes() ), GetBuildRequest.class );

        GetBuildRequest expected = new GetBuildRequest( "org.dashbuilder-dashbuilder-parent-metadata-0.4.0.Final-1" );

        assertEquals( expected.getNvr(), parsed.getNvr() );

        GetBuildRequest rounded =
                        new RWXMapper().parse( new ByteArrayInputStream( new RWXMapper().render( parsed ).getBytes() ),
                                               GetBuildRequest.class );

        assertEquals( expected.getNvr(), rounded.getNvr() );
    }

    @Test
    public void roundTrip_GetBuildResponse() throws Exception
    {
        String source = getXMLString( "kojiGetBuildResponse" );

        GetBuildResponse parsed =
                        new RWXMapper().parse( new ByteArrayInputStream( source.getBytes() ), GetBuildResponse.class );

        assertGetBuildResponse( parsed );

        GetBuildResponse rounded =
                        new RWXMapper().parse( new ByteArrayInputStream( new RWXMapper().render( parsed ).getBytes() ),
                                               GetBuildResponse.class );

        assertGetBuildResponse( rounded );
    }

    private void assertGetBuildResponse( GetBuildResponse response )
    {
        KojiBuildInfo buildInfo = response.getBuildInfo();

        assertEquals( 513598, buildInfo.getBuildId() );
        assertEquals( 48475, buildInfo.getPackageId() );
        assertEquals( "org.dashbuilder-dashbuilder-parent-metadata", buildInfo.getName() );
        assertEquals( "1", buildInfo.getRelease() );
        assertEquals( "0.4.0.Final_10", buildInfo.getVersion() );
        assertEquals( null, buildInfo.getExtra() );
    }

    @Test
    public void roundTrip_ListBuildResponseNIL() throws Exception
    {
        String source = getXMLStringIgnoreFormat( "kojiListBuildsResponseNIL" );
        ListBuildResponse parsed =
                        new RWXMapper().parse( new ByteArrayInputStream( source.getBytes() ), ListBuildResponse.class );

        assertEquals( null, parsed.getBuilds() );

        String rendered = new RWXMapper().render( parsed );

        assertEquals( formalizeXMLString( source ), formalizeXMLString( rendered ) );
    }

    @Test
    public void roundTrip_ListTagsResponse() throws Exception
    {
        String source = getXMLString( "kojiListTagsResponse" );
        ListTagsResponse parsed =
                        new RWXMapper().parse( new ByteArrayInputStream( source.getBytes() ), ListTagsResponse.class );

        assertListTagsResponse( parsed );

        ListTagsResponse rounded =
                        new RWXMapper().parse( new ByteArrayInputStream( new RWXMapper().render( parsed ).getBytes() ),
                                               ListTagsResponse.class );

        assertListTagsResponse( rounded );
    }

    private void assertListTagsResponse( ListTagsResponse response )
    {
        List<KojiTagInfo> tags = response.getTags();

        assertEquals( 4, tags.size() );

        for ( KojiTagInfo tag : tags )
        {
            assertEquals( Boolean.FALSE, tag.getLocked() );
            assertEquals( true, tag.getMavenSupport() );
            assertEquals( true, tag.getMavenIncludeAll() );
            assertEquals( null, tag.getPermission() );
            assertEquals( null, tag.getPermissionId() );
            assertEquals( null, tag.getArches() );
        }

        KojiTagInfo tag0 = tags.get( 0 );
        KojiTagInfo tag1 = tags.get( 1 );
        KojiTagInfo tag2 = tags.get( 2 );
        KojiTagInfo tag3 = tags.get( 3 );

        assertEquals( 8829, tag0.getId() );
        assertEquals( 8913, tag1.getId() );
        assertEquals( 9761, tag2.getId() );
        assertEquals( 10559, tag3.getId() );

        assertEquals( "jb-bxms-6.3-candidate", tag0.getName() );
        assertEquals( "jb-cs-maven-candidate", tag1.getName() );
        assertEquals( "jb-fis-2.0-maven-imports", tag2.getName() );
        assertEquals( "jb-mm-7.0-maven-candidate", tag3.getName() );
    }

}
