/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.cache.filter.support;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.elasticsearch.common.Unicode;

import java.io.IOException;
import java.util.Arrays;

public interface CacheKeyFilter {

    public static class Key {

        private final byte[] bytes;

        // we pre-compute the hashCode for better performance (especially in IdCache)
        private final int hashCode;

        public Key(byte[] bytes) {
            this.bytes = bytes;
            this.hashCode = Arrays.hashCode(bytes);
        }

        public Key(String str) {
            this(Unicode.fromStringAsBytes(str));
        }

        public byte[] bytes() {
            return this.bytes;
        }

        public String utf8ToString() {
            return Unicode.fromBytes(bytes);
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o.getClass() != this.getClass()) {
                return false;
            }
            Key bytesWrap = (Key) o;
            return Arrays.equals(bytes, bytesWrap.bytes);
        }

        @Override public int hashCode() {
            return hashCode;
        }
    }

    public static class Wrapper extends Filter implements CacheKeyFilter {

        private final Filter filter;

        private final Key key;

        public Wrapper(Filter filter, Key key) {
            this.filter = filter;
            this.key = key;
        }

        @Override public Key cacheKey() {
            return key;
        }

        @Override public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
            return filter.getDocIdSet(reader);
        }

        @Override public int hashCode() {
            return filter.hashCode();
        }

        @Override public boolean equals(Object obj) {
            return filter.equals(obj);
        }

        @Override public String toString() {
            return filter.toString();
        }
    }

    Key cacheKey();
}