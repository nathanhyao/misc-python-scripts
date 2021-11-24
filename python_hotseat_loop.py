###############################################################################
# Automates answering polls on Purdue Hotseat. Don't use this script. Cutting
# corners is practically never a good idea, I found out last Thursday.
###############################################################################

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.alert import Alert
from selenium.webdriver.support import expected_conditions as EC
import getpass
import time

# Selenium documentation for Python: https://selenium-python.readthedocs.io/

PATH = "C:\Program Files (x86)\Chrome Driver\chromedriver.exe"
chrome_options = webdriver.ChromeOptions()
chrome_options.add_experimental_option('excludeSwitches', ['enable-logging'])
driver = webdriver.Chrome(PATH, chrome_options=chrome_options) # w/ errors off

# Goes to HotSeat.
driver.get("https://www.openhotseat.org/Login?ReturnUrl=https%3a%2f%2fwww.openhotseat.org%2f")

print("current page: " + driver.title)

# Chrome browser has been opened. Navigate to login screen.
try:
    # It's a good idea to find the parent "container" of the wanted element.
    # Beware that code may execute before page is reached (error).

    # search for desired webpage element for 10 seconds
    login_container = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "login"))
    )
    login_button = login_container.find_element(By.CLASS_NAME, "hover-shadow")
    login_button.click()
except Exception:
    print("ERROR: Unable to load login screen!")
    driver.quit()

print("current page: " + driver.title)

# User must enter valid credentials and perform two-factor authentication.
print("Complete Purdue DUO Two-Factor Authentication.\n",
      "Enter valid credentials:")

username = input("Username: ")
password = getpass.getpass("Password: ")

username_field = driver.find_element(By.ID, "username")
username_field.send_keys(username)

password_field = driver.find_element(By.ID, "password")
password_field.send_keys(password)

submit_container = driver.find_element(By.CLASS_NAME, "submit")
submit_button = submit_container.find_element(By.NAME, "submit")
submit_button.click()

print("current page: " + driver.title)

# After user performs two-factor authentication, search for "Science" section.
try:
    hotseat_content = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "content"))
    )
    topic_container = hotseat_content.find_element(By.ID, "private")
    science_button = topic_container.find_element(By.LINK_TEXT, "Science")
    science_button.click()
except Exception:
    print("ERROR: Unable to load HotSeat content!")
    driver.quit()

print("current page: " + driver.title)

# Script has made it to the "Science" page. Now navigate to "Polls".
try:
    science_content = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "subnav-wrapper"))
    )
    science_poll_button = science_content.find_element(By.LINK_TEXT, "Polls")
    science_poll_button.click()
except Exception:
    print("ERROR: Unable to load Science Polls page!")
    driver.quit()

print("current page: " + driver.title)

# Script has made it to the "Polls" page. Now check for active polls.
while True:
    print("Searching for active polls...")
    try:
        poll_answer_button = WebDriverWait(driver, 4200).until(
            EC.presence_of_element_located((By.LINK_TEXT, "A"))
        )
        poll_answer_button.click()

        time.sleep(5) # wait for "response updated" alert and close it
        alert = Alert(driver)
        print(alert.text)
        alert.accept()

        time.sleep(10) # sleep 10 seconds to avoid overloading active poll
    except Exception:
        print("ERROR: Unable to find any polls!")
        driver.quit()
        break

print("Program ending...")