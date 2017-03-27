/*
 * Copyright 2014 William Seemann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wseemann.media.jplaylistparser.parser.xspf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import wseemann.media.jplaylistparser.exception.JPlaylistParserException;
import wseemann.media.jplaylistparser.mime.MediaType;
import wseemann.media.jplaylistparser.parser.AbstractParser;
import wseemann.media.jplaylistparser.playlist.Playlist;
import wseemann.media.jplaylistparser.playlist.PlaylistEntry;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;

public class XSPFPlaylistParser extends AbstractParser {
	public final static String EXTENSION = ".xspf";
    
	private final String LOCATION_ELEMENT = "LOCATION";
	private final String TITLE_ELEMENT = "TITLE";
	private final String TRACK_ELEMENT = "TRACK";
	private final String TRACKLIST_ELEMENT = "TRACKLIST";
	
	private static int mNumberOfFiles = 0;
	
    private static final Set<MediaType> SUPPORTED_TYPES =
    		Collections.singleton(MediaType.video("application/xspf+xml"));

    public Set<MediaType> getSupportedTypes() {
    	return SUPPORTED_TYPES;
    }
	
	/**
	 * Retrieves the files listed in a .asx file
	 * @throws IOException 
	 */
    private void parsePlaylist(InputStream stream, Playlist playlist) throws IOException {
		String xml = "";
        String line = null;
        BufferedReader reader = null;
        
		// Start the query
        reader = new BufferedReader(new InputStreamReader(stream));
		    
		while ((line = reader.readLine()) != null) {
		    xml = xml + line; 
        }
		    
		parseXML(xml, playlist);
    }

	private void parseXML(String xml, Playlist playlist) {
		SAXBuilder builder = new SAXBuilder();
	    Reader in;
	    Document doc = null;
	    Element root = null;
	    
	    try {
		    in = new StringReader(xml);
		    
	    	doc = builder.build(in);
	    	root = doc.getRootElement();
	    	List<Element> children = castList(Element.class, root.getChildren());
	    	
	    	for (int i = 0; i < children.size(); i++) {
	    		String tag = children.get(i).getName();
	    		
	    		if (tag != null && tag.equalsIgnoreCase(TRACKLIST_ELEMENT)) {
	    			List<Element> children2 = castList(Element.class, children.get(i).getChildren());
	    			
	    			for (int j = 0; j < children2.size(); j++) {
	    				String attributeName = children2.get(j).getName();
	        		
	        	    	if (attributeName.equalsIgnoreCase(TRACK_ELEMENT)) {
	        	    		List<Element> children3 = castList(Element.class, children2.get(j).getChildren());
	        	    		buildPlaylistEntry(children3, playlist);
	        	    	}
	    			}
	    		}
	    	}
	    } catch (JDOMException e) {
	    } catch (IOException e) {
	    } catch (Exception e) {
	    }
	}
    
    private void buildPlaylistEntry(List<Element> children, Playlist playlist) {
    	PlaylistEntry playlistEntry = new PlaylistEntry();
    	
    	for(int i = 0; i < children.size(); i++) {
    	    String attributeName = children.get(i).getName();
    		
    	    if (attributeName.equalsIgnoreCase(LOCATION_ELEMENT)) {
    	    	String href = children.get(i).getValue();
    	    	
    	    	// TODO: add trim?
    	    	playlistEntry.set(PlaylistEntry.URI, href);
    	    } else if (attributeName.equalsIgnoreCase(TITLE_ELEMENT)) {
    	    	String title = children.get(i).getValue();
    	    	
    	    	if (title != null) {
		        	playlistEntry.set(PlaylistEntry.PLAYLIST_METADATA, title);
    	    	}
    	    }
    	}
    	
    	mNumberOfFiles = mNumberOfFiles + 1;
    	playlistEntry.set(PlaylistEntry.TRACK, String.valueOf(mNumberOfFiles));
    	parseEntry(playlistEntry, playlist);
    }
    
    private <T> List<T> castList(Class<? extends T> castClass, List<?> c) {
        List<T> list = new ArrayList<T>(c.size());
        
        for(Object o: c) {
        	list.add(castClass.cast(o));
        }
        
        return list;
    }

	@Override
	public void parse(String uri, InputStream stream, Playlist playlist)
			throws IOException, SAXException, JPlaylistParserException{
		parsePlaylist(stream, playlist);
	}
}

