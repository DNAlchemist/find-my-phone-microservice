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


import geb.Browser
import org.openqa.selenium.By
import org.slf4j.LoggerFactory
import ratpack.form.Form

import java.util.concurrent.ConcurrentLinkedQueue

import static org.openqa.selenium.By.xpath
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

def browsers = [] as ConcurrentLinkedQueue<Browser>
def log = LoggerFactory.getLogger("ratpack.groovy");

ratpack {
    handlers {
        get "health", {
            render "ok"
        }

        get "android/devices", { ctx ->
            def browser = browsers.empty ? new Browser() : browsers.poll()
            try {
                browser.go "https://myaccount.google.com/find-your-phone"

                if (browser.currentUrl == "https://myaccount.google.com/intro/find-your-phone") {
                    browser.authenticate(request.auth.username, request.auth.password)
                }

                browser.waitFor {
                    browser.find xpath("//div[@role='list']/div/div") iterator() hasNext()
                }

                def info = browser
                        .find(deviceXPath())
                        .iterator()
                        .collect({ [name: it.attr("aria-label")] })

                ctx.render(json(devices: info))

                browsers << browser
            } catch (e) {
                log.error e.message, e
                close browser
                ctx.response.status 500
                ctx.render e.message
                return null
            }
        }

        post "android/devices/ring", { ctx ->
            ctx.parse(Form).then { form ->
                def browser = browsers.empty ? new Browser() : browsers.poll()
                try {
                    browser.go "https://myaccount.google.com/find-your-phone"

                    if (browser.currentUrl == "https://myaccount.google.com/intro/find-your-phone") {
                        browser.authenticate(request.auth.username, request.auth.password)
                    }

                    browser.waitFor {
                        browser.find xpath("//div[@role='list']/div/div") iterator() hasNext()
                    }

                    browser
                            .find(deviceXPath(form.name))
                            .first()
                            .click()

                    if (browser.isPasswordPage()) {
                        browser.authenticateWithPassword(request.auth.password)
                    }

                    browser.waitFor {
                        browser.find xpath("//div[text()='Прозвонить']") iterator() hasNext()
                    }

                    browser
                            .find(xpath("//div[text()='Прозвонить']"))
                            .click()

                    ctx.response.status 200
                    ctx.response.send()
                } catch (e) {
                    log.error e.message, e
                    close browser
                    ctx.response.status 500
                    ctx.render e.message
                }
            }
        }

    }

}

static close(Browser browser) {
    if (browser) {
        try {
            browser.close()
        } catch (ignored) {
        }
    }
}

static By deviceXPath(String s) {
    return xpath("//div[@role='list']/div/div" + (s ? "[@aria-label='${s}']" : ""))
}
