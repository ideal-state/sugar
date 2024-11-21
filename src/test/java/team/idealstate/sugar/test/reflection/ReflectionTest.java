/*
 *    Copyright 2024 ideal-state
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

package team.idealstate.sugar.test.reflection;

import org.junit.jupiter.api.Test;
import team.idealstate.sugar.reflection.exception.ReflectionException;
import team.idealstate.sugar.test.reflection.entity.Human;
import team.idealstate.sugar.test.reflection.entity.ReflectHuman;

import static org.junit.jupiter.api.Assertions.*;
import static team.idealstate.sugar.reflection.Reflection.reflect;

public class ReflectionTest {

    private static final String NAME = "John";
    private static final String CHANGED_NAME = "Bob";
    private static final int JUVENILE_AGE = 16;
    private static final int ADULT_AGE = 30;

    @Test
    public void newInstance() {
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, null);
        Human human = reflectHuman.newInstance(NAME, JUVENILE_AGE);
        assertEquals(new Human(NAME, JUVENILE_AGE), human);
    }

    @Test
    public void name() {
        Human human = new Human(NAME, JUVENILE_AGE);
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, human);
        assertEquals(NAME, reflectHuman.name());
    }

    @Test
    public void age() {
        Human human = new Human(NAME, JUVENILE_AGE);
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, human);
        assertEquals(JUVENILE_AGE, reflectHuman.age());
    }

    @Test
    public void getAndSetAge() {
        Human human = new Human(NAME, JUVENILE_AGE);
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, human);
        assertEquals(JUVENILE_AGE, reflectHuman.getAndSetAge(ADULT_AGE));
        assertEquals(ADULT_AGE, human.age);
    }

    @Test
    public void getName() {
        Human human = new Human(NAME, JUVENILE_AGE);
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, human);
        assertEquals(NAME, reflectHuman.getName());
    }

    @Test
    public void setName() {
        Human human = new Human(NAME, JUVENILE_AGE);
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, human);
        assertThrowsExactly(ReflectionException.class, () -> reflectHuman.setName(CHANGED_NAME));
    }

    @Test
    public void getAge() {
        Human human = new Human(NAME, JUVENILE_AGE);
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, human);
        assertEquals(JUVENILE_AGE, reflectHuman.getAge());
    }

    @Test
    public void setAge() {
        Human human = new Human(NAME, JUVENILE_AGE);
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, human);
        reflectHuman.setAge(ADULT_AGE);
        assertEquals(ADULT_AGE, human.age);
    }

    @Test
    public void LEGAL_AGE() {
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, null);
        assertThrowsExactly(ReflectionException.class, reflectHuman::LEGAL_AGE);
    }

    @Test
    public void getLegalAge() {
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, null);
        assertEquals(Human.getLegalAge(), reflectHuman.getLegalAge());
    }

    @Test
    public void defaultMethod() {
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, null);
        assertDoesNotThrow(reflectHuman::defaultMethod);
    }

    @Test
    public void unimplementedMethod() {
        ReflectHuman reflectHuman = reflect(null, ReflectHuman.class, null);
        assertThrowsExactly(ReflectionException.class, reflectHuman::unimplementedMethod);
    }
}
