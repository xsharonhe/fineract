/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.integrationtests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.GlobalConfigurationHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.savings.SavingsAccountHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class InvalidBirthdayParamClientSavingsIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private final String invalidBirthday;

    @Parameters
    public static Collection<Object[]> invalidBirthday() {
        return Arrays.asList(new Object[][] { { "" }, // empty
                { "13-10" }, // 13 is not a valid month
                { "12-32" }, // 32 is not a valid day
                { "abcd" }, // alphanumeric and not date
                { "1231" }, // no delimiter
                { "1-12" }, // month is not zero padded
                { "12-1" }, // day is not zero padded
                { "12-1-2024" } // full date format
        });
    }

    public InvalidBirthdayParamClientSavingsIntegrationTest(String invalidBirthday) {
        this.invalidBirthday = invalidBirthday;
    }

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.requestSpec.header("Fineract-Platform-TenantId", "default");
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @After
    public void tearDown() {
        GlobalConfigurationHelper.resetAllDefaultGlobalConfigurations(this.requestSpec, this.responseSpec);
        GlobalConfigurationHelper.verifyAllDefaultGlobalConfigurations(this.requestSpec, this.responseSpec);
    }

    @Test
    public void testSavingsInvalidBirthdayParam() {
        SavingsAccountHelper savingsAccountHelperValidationError = new SavingsAccountHelper(this.requestSpec,
                new ResponseSpecBuilder().build());
        List<HashMap> error = (List<HashMap>) savingsAccountHelperValidationError.getSavingsAccounts(invalidBirthday,
                CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.savingsaccount.birthday.does.not.match.regexp",
                error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    }
}
