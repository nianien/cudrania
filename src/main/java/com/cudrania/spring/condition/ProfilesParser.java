/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cudrania.spring.condition;


import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Predicate;

/**
 * @author Phillip Webb
 * @since 5.1
 */
final class ProfilesParser {

    private ProfilesParser() {
    }


    public static Profiles parse(String expression) {
        StringTokenizer tokens = new StringTokenizer(expression, "()&|!", true);
        return parseTokens(expression, tokens);
    }

    private static Profiles parseTokens(String expression, StringTokenizer tokens) {
        List<Profiles> elements = new ArrayList<>();
        Operator operator = null;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            if (token.isEmpty()) {
                continue;
            }
            switch (token) {
                case "(":
                    elements.add(parseTokens(expression, tokens));
                    break;
                case "&":
                    assertWellFormed(expression, operator == null || operator == Operator.AND);
                    operator = Operator.AND;
                    break;
                case "|":
                    assertWellFormed(expression, operator == null || operator == Operator.OR);
                    operator = Operator.OR;
                    break;
                case "!":
                    elements.add(not(parseTokens(expression, tokens)));
                    break;
                case ")":
                    Profiles merged = merge(expression, elements, operator);
                    elements.clear();
                    elements.add(merged);
                    operator = null;
                    break;
                default:
                    elements.add(equals(token));
            }
        }
        return merge(expression, elements, operator);
    }

    private static Profiles merge(String expression, List<Profiles> elements, Operator operator) {
        assertWellFormed(expression, !elements.isEmpty());
        if (elements.size() == 1) {
            return elements.get(0);
        }
        Profiles[] profiles = elements.toArray(new Profiles[0]);
        return (operator == Operator.AND ? and(profiles) : or(profiles));
    }

    private static void assertWellFormed(String expression, boolean wellFormed) {
        Assert.isTrue(wellFormed, "Malformed profile expression [" + expression + "]");
    }

    private static Profiles or(Profiles... profiles) {
        return activeProfile -> Arrays.stream(profiles).anyMatch(isMatch(activeProfile));
    }

    private static Profiles and(Profiles... profiles) {
        return activeProfile -> Arrays.stream(profiles).allMatch(isMatch(activeProfile));
    }

    private static Profiles not(Profiles profiles) {
        return activeProfile -> !profiles.matches(activeProfile);
    }

    private static Profiles equals(String profile) {
        return activeProfile -> activeProfile.test(profile);
    }

    private static Predicate<Profiles> isMatch(Predicate<String> activeProfile) {
        return profiles -> profiles.matches(activeProfile);
    }


    public interface Profiles {

        /**
         * Test if this {@code Profiles} instance <em>matches</em> against the given
         * active profiles predicate.
         *
         * @param activeProfiles predicate that tests whether a given profile is
         *                       currently active
         */
        boolean matches(Predicate<String> activeProfiles);

    }

}
