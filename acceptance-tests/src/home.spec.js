const puppeteerWrapper = require("./config/puppeteer-wrapper");
const frontendConfig = require("./config/frontend-config");

let browser, page;

describe(frontendConfig.url, () => {
  beforeEach(async () => {
    browser = await puppeteerWrapper.launch();
    page = await browser.newPage();
  });

  afterEach(async () => {
    browser.close();
  });

  describe("Landing page", () => {

    beforeEach(async () => {
      await page.goto(frontendConfig.url, { waitUntil: "networkidle2" });
    });

    describe("Page title", () => {
      it("equals Ui", async () => {
        const title = await page.title();
        expect(title).toEqual("Ui");
      });
    });
  });
});