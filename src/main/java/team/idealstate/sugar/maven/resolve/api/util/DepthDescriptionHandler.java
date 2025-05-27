/*
 *    Copyright 2025 ideal-state
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package team.idealstate.sugar.maven.resolve.api.util;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

public abstract class DepthDescriptionHandler extends DefaultHandler {

    private final Deque<String> depthDescription = new LinkedList<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        depthDescription.addLast(qName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        depthDescription.removeLast();
        super.endElement(uri, localName, qName);
    }

    protected final boolean isMatched(@NotNull Collection<String> depthDescription) {
        Validation.notNull(depthDescription, "Depth cannot be null");
        if (depthDescription.size() != this.depthDescription.size()) return false;
        Iterator<String> iterator = this.depthDescription.iterator();
        for (String qName : depthDescription) {
            if (!Objects.equals(qName, iterator.next())) {
                return false;
            }
        }
        return true;
    }

    protected final boolean isParentMatched(@NotNull Collection<String> depthDescription) {
        Validation.notNull(depthDescription, "Depth cannot be null");
        if (depthDescription.size() != this.depthDescription.size() - 1) return false;
        Iterator<String> iterator = this.depthDescription.iterator();
        for (String qName : depthDescription) {
            if (!Objects.equals(qName, iterator.next())) {
                return false;
            }
        }
        return true;
    }

    protected final String currentQName() {
        return depthDescription.peekLast();
    }
}
