/*
 * MIT License
 *
 * Copyright (c) 2017 Mikhalev Ruslan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package one.chest

import geb.Browser
import groovy.util.logging.Slf4j
import ratpack.http.Request

import static org.openqa.selenium.By.xpath
import static org.openqa.selenium.By.xpath
import static org.openqa.selenium.By.xpath
import static org.openqa.selenium.By.xpath
import static org.openqa.selenium.By.xpath
import static org.openqa.selenium.By.xpath
import static org.openqa.selenium.By.xpath

@Slf4j
class AuthenticateExtension {

    static authenticate(Browser browser, String login, String password) {
        browser.waitFor {
            browser.find xpath("//div[@role='presentation']") displayed
        }
        browser.find xpath("//div[@role='presentation']") click()

        browser.find xpath("//input[@type='email']") value login
        browser.find xpath("//div[@id='identifierNext']") click()
        authenticateWithPassword(browser, password)
    }

    static boolean isPasswordPage(Browser browser) {
        return browser.find(xpath('''//p[text()="To continue, first verify it's you"]''')).size()
    }

    static authenticateWithPassword(Browser browser, String password) {
        browser.waitFor {
            browser.find xpath("//input[@type='password']") displayed
        }
        browser.find xpath("//input[@type='password']") value password
        browser.find xpath("//div[@id='passwordNext']") click()
    }

    static Map<String, String> getAuth(Request request) {
        String authHeader = request.headers["Authorization"]
        if(!authHeader) {
            throw new RuntimeException("Authentication failed")
        }

        def (username, password) = new String(authHeader.split(" ")[1].decodeBase64()).split(":")
        return [username: username, password: password] as Map<String, String>
    }
}
