### Emulation 
An open-source emulation of RuneScape as it was on January 28, 2009, fork of [2009scape](https://gitlab.com/2009scape/2009scape).


[![License][license-shield]][license-url] [![Release][libs]][libs-url] [![Release][play-release]][play-url]<br>

___

#### Table of Contents

- [Requirements](#essentials)
- [Forking the Repository](#forking-the-repository)
- [Cloning the Repository](#cloning-the-repository)
- [Importing into IntelliJ IDEA](#import-into-intellij-idea)
- [Setting Up the Project](#setting-up-the-project)
- [Running the Application](#launching)
- [Setting Up GitLab with SSH](#gitlab-vcs-in-intellij-idea)
- [Configuring Single-Player Mode](#setting-up-the-single-player-mode)
- [License Information](#license)

---

#### Essentials

_For Windows users_ - Enable developer mode in Windows settings first.

Make sure you have the following installed:

- **Java 11**: You can download it from [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
  or [AdoptOpenJDK](https://adoptium.net/temurin/releases/?version=11).
- **IntelliJ IDEA**: Download and install from [JetBrains](https://www.jetbrains.com/idea/download/).

---

#### Forking the Repository

1. Click the **Fork** button at the top right to create a personal copy of the repository.

___

#### Cloning the Repository

1. After forking the repository, go to your fork’s page.
2. Click the **Clone** button and copy the URL (either HTTPS or SSH).
3. Open a terminal or command prompt and run the following command to clone the repository.

```bash
git clone <your-fork-url>
```

#### Import into IntelliJ IDEA

1. Open **IntelliJ IDEA**.
2. Go to **File > New > Project from Version Control > Git**.
3. Paste the cloned repository URL into the **URL** field.
4. Select the directory where you want to store the project and click **Clone**.

---

#### GitLab VCS in IntelliJ IDEA

**Generate an SSH key** (if you don’t already have one):

```bash
ssh-keygen -t ed25519 -C "<comment>"
```

Add the **SSH key** to your **GitLab** account.

**Configure Git**:

- Set your GitLab username: `git config --global user.name "Your Name"`
- Set your GitLab email: `git config --global user.email "your_email@example.com"`

---

#### Building the Project

1. Open the **Terminal** inside IntelliJ IDEA (or use an external terminal).
2. Navigate to the project’s root directory if you're not already there.
3. Run the following command to build the project:

```bash
mvn clean install
```

#### Setting Up the Project

1. Open project in IntelliJ IDEA, navigate to **File > Settings** (or **Preferences** on macOS).
2. Under **Build, Execution, Deployment > Build Tools**, make sure **Maven** is properly configured to use your
   installed version.
3. Ensure that Java 11 or later is selected as the **Project SDK**.
4. Sync the Maven project by clicking on the **Maven** tab on the right and then clicking the **Refresh** button.

---

#### Setting up the Single-Player Mode

1. Download the [GitHub Desktop](https://desktop.github.com/download/) app.
2. Go to the [singleplayer](https://github.com/szumaster10/530-game) repository.
3. Fork this repository to your own repositories, then clone it using the GitHub Desktop app or download the repository.
4. Run `launch.bat` on Windows or `launch.sh` on a UNIX-based system.
5. If the server starts successfully, run `client.jar`.

---

#### Launching

To launch the project, you can use Maven with the following command:

```bash
mvn exec:java -f pom.xml
```

This will compile and run the Java application based on the configuration in the `pom.xml` file.

#### License

- This project is licensed under the **AGPL 3.0**. This means you are free to modify, distribute, and use this code,
  provided you comply with the terms of the AGPL 3.0 license.
- You can find more information about the license [here](https://www.gnu.org/licenses/agpl-3.0.html).
- The license applies to the entire repository unless otherwise stated.

[license-shield]: https://img.shields.io/badge/license-AGPL--3.0-informational

[license-url]: https://www.gnu.org/licenses/agpl-3.0.en.html

[fork-shield]: https://img.shields.io/badge/repository-fork-blue

[fork-url]: https://gitlab.com/2009scape/2009scape

[libs]: https://img.shields.io/badge/constants-library-blue

[libs-url]: https://gitlab.com/rs2-emu/530-variables

[play-release]: https://img.shields.io/badge/singleplayer-release-blue

[play-url]: https://gitlab.com/rs2-emu/530-game