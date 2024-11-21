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

package team.idealstate.sugar.test.function;

import org.junit.jupiter.api.Test;
import team.idealstate.sugar.function.Functional;
import team.idealstate.sugar.test.function.entity.CloseableRobot;
import team.idealstate.sugar.test.function.entity.Robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static team.idealstate.sugar.function.Functional.functional;

public class FunctionalTest {

    private static final String NAME = "John";
    private static final String CHANGED_NAME = "Bob";
    private static final int JUVENILE_AGE = 16;
    private static final int LEGAL_AGE = 18;
    private static final int ADULT_AGE = 30;

    private static boolean isAdult(Robot robot) {
        return robot.getAge() >= LEGAL_AGE;
    }

    @Test
    public void isNull() {
        boolean isNull = functional(null).isNull();
        assertTrue(isNull);
    }

    @Test
    public void isNotNull() {
        boolean isNotNull = functional(new Robot()).isNotNull();
        assertTrue(isNotNull);
    }

    @Test
    public void apply() {
        Functional<Robot> functional = functional(new Robot());
        Robot robot = functional.apply(it -> {
            it.setName(NAME);
            it.setAge(JUVENILE_AGE);
        }).it();
        assertEquals(new Robot(NAME, JUVENILE_AGE), robot);

        functional.apply(it -> {
            it.setName(CHANGED_NAME);
            it.setAge(ADULT_AGE);
        });
        assertEquals(new Robot(CHANGED_NAME, ADULT_AGE), robot);
    }

    @Test
    public void run() {
        Robot robot = new Robot();
        functional(robot).run(it -> {
            it.setName(NAME);
            it.setAge(JUVENILE_AGE);
        });
        assertEquals(new Robot(NAME, JUVENILE_AGE), robot);
    }

    @Test
    public void use() {
        CloseableRobot robot = new CloseableRobot();
        functional(robot).use(it -> {
            it.setName(NAME);
            it.setAge(JUVENILE_AGE);
        });
        assertEquals(new CloseableRobot(NAME, JUVENILE_AGE), robot);
        assertTrue(robot.isClosed());
    }

    @Test
    public void convert() {
        Robot robot = new Robot(NAME, JUVENILE_AGE);
        String name = functional(robot)
                .convert(Robot::getName)
                .it();
        assertEquals(NAME, name);
    }

    @Test
    public void when() {
        Robot robot = new Robot();
        Functional<Robot> functional = functional(robot)
                .apply(it -> {
                    it.setName(NAME);
                    it.setAge(JUVENILE_AGE);
                })
                .when(FunctionalTest::isAdult);
        functional.run(it -> it.setName(CHANGED_NAME));
        assertEquals(new Robot(NAME, JUVENILE_AGE), robot);

        functional(robot).apply(it -> it.setAge(ADULT_AGE))
                .when(FunctionalTest::isAdult)
                .run(it -> it.setName(CHANGED_NAME));
        assertEquals(new Robot(CHANGED_NAME, ADULT_AGE), robot);
    }
}
