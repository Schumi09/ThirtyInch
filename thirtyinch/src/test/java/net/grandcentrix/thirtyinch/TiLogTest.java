/*
 * Copyright (C) 2017 grandcentrix GmbH
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.grandcentrix.thirtyinch;

import org.junit.Test;

import static junit.framework.Assert.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class TiLogTest {

    @Test
    public void preventSettingRecursiveLogger() throws Exception {
        try {
            TiLog.setLogger(TiLog.TI_LOG);
            fail("did not throw");
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Recursion");
        }
    }
}