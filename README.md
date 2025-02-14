### Emulation

An open-source emulation of RuneScape as it was on Jan 28, 2009.

___

[![License][license-shield]][license-url] [![Fork][fork-shield]][fork-url] [![Release][libs]][libs-url] [![Release][play-release]][play-url]<br>

- The repository is licensed under the **AGPL 3.0 License**.
- The license applies to the entire repository unless otherwise specified.
- The full license details can be found [here](https://www.gnu.org/licenses/agpl-3.0.en.html).

___

#### Table of Contents

* [Prerequisites](#prerequisites)
* [Forking the Repository](#forking-the-repository)
* [Cloning the Repository](#cloning-the-repository)
* [Import into IntelliJ IDEA](#import-into-intellij-idea)
* [GitLab VCS in IntelliJ IDEA](#gitlab-vcs-in-intellij-idea)
* [Setting Up the Project](#setting-up-the-project)
* [Git Installation setup](#git-installation-setup)
    * [Git on Windows](#git-on-windows)
    * [Git on Linux](#git-on-linux)
    * [Git on macOS](#git-on-macos)
* [Launching](#launching)
* [Single-Player](#setting-up-the-single-player)
* [Troubleshooting](#troubleshooting)
* [Acknowledgments](#acknowledgments)
* [FAQ](#faq)

***

#### Prerequisites

_For Windows users_ - Turn developer mode on first in Windows developer settings.

- Create a GitLab account.

Make sure you have the following installed:

- **Java 11**: You can download it from [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
  or [AdoptOpenJDK](https://adoptium.net/temurin/releases/?version=11).
- **IntelliJ IDEA**: Download and install from [JetBrains](https://www.jetbrains.com/idea/download/).

#### Forking the Repository

1. Click on the **Fork** button on the top right to create a personal copy of the repository.

#### Cloning the Repository

1. Once you’ve forked the repository, navigate to your fork’s page.
2. Click the **Clone** button and copy the URL (either HTTPS or SSH).
3. Open a terminal or command prompt and run the following command to clone the repository:

```bash
git clone <your-fork-url>
```

#### Import into IntelliJ IDEA

1. Open **IntelliJ IDEA**.
2. Go to **File > New > Project from Version Control > Git**.
3. Paste the cloned repository URL into the **URL** field.
4. Select the directory where you want to store the project and click **Clone**.

#### GitLab VCS in IntelliJ IDEA

**Generate SSH key** (if you don't already have one):

```bash
ssh-keygen -t ed25519 -C "<comment>"
```

Add the **SSH key** to your **GitLab** account:

- Copy the public key: `cat ~/.ssh/id_ed25519.pub | clip`.
- Go to GitLab SSH Keys and paste the key.

**Configure Git**:

- Set your GitLab username: `git config --global user.name "Your Name"`
- Set your GitLab email: `git config --global user.email "your_email@example.com"`

**Set the remote URL** for your repository:

```bash
git remote set-url origin git@gitlab.com:<your-username>/your-fork-repository.git
```

_Now you can push and pull changes securely using SSH._

#### Building the Project

Once everything is set up, you can build the project using Maven:

1. Open the **Terminal** inside IntelliJ IDEA (or use an external terminal).
2. Navigate to the project’s root directory if you're not already there.
3. Run the following command to build the project:

```bash
mvn clean install
```

---

#### Setting Up the Project

1. Once the project is open in IntelliJ IDEA, navigate to **File > Settings** (or **Preferences** on macOS).
2. Under **Build, Execution, Deployment > Build Tools**, ensure that **Maven** is properly configured to use your
   installed version.
3. Make sure that Java 11 or later is selected as the **Project SDK**.
4. Sync the Maven project by clicking on the **Maven** tab on the right and then clicking the **Refresh** button.

#### Git Installation setup

<details>
  <summary>Click to reveal</summary>

#### Git on Windows

<details>
  <summary>Click to reveal</summary>

---

1. Download

Visit the [official Git website](https://git-scm.com/download/win) to download the latest version of the Git installer
for Windows. The download should start automatically when you visit the page.

2. Run the Installer

Launch the downloaded installer and follow the installation wizard.

3. Verify the install via _Git Bash_

To ensure that Git has been installed correctly, open Git Bash and type the following command:

```
git --version
```

Press **Enter**, and the name of the version of Git you just installed should appear.

</details>

---

#### Git on macOS

<details>
  <summary>Click to reveal</summary>

---

1. Download

Visit the [official Git website](https://git-scm.com/download/mac) to download the latest version of the Git installer
for macOS.

2. Complete Installation Instructions

Once the installer is downloaded, open the .dmg file and follow the installation instructions.

3. Verify Installation With Terminal

To ensure that Git has been installed correctly, open Terminal and type the following command:

```
git --version
```

Press Enter, and you should see the version of Git you installed displayed on the next line.

</details>

---

#### Git on Linux

<details>

  <summary>Click to reveal</summary>

---

1. Install Via Package Manager

The easiest way to install Git on Linux is through the package manager for your distribution. For Debian-based
distributions like Ubuntu, you can use the apt package manager:

```
sudo apt-get install git
```

2. Verify the Installation

---

Open Terminal and type in the following:

```
git --version
```

</details>
</details>

---

#### Setting up the single-player

<details>

  <summary>Click to reveal</summary>

---

1. Download [GitHub Desktop](https://desktop.github.com/download/) app.
2. Go to [singleplayer](https://github.com/szumaster1/530-game) repository.
3. Fork this repository to your own repositories, then clone it using the GitHub Desktop app or simply download the repository.
4. Run `launch.bat` on Windows or `launch.sh` on a UNIX-based system.
5. If the server starts successfully, run `client.jar`

</details>

---

#### Launching

- All information about the launch can be found [here](https://www.mojohaus.org/exec-maven-plugin/).

#### Troubleshooting

- If you encounter issues during setup or while running the server, please refer to the GitLab issues page or open a new
  issue if necessary.

---

#### Acknowledgments

- This project is a fork of the [2009scape](https://gitlab.com/2009scape/2009scape) project.

---

#### FAQ

<details>
  <summary>Click to reveal</summary>

---

#### Admin rights

You'll need to edit default config and set `noauth_default_admin = false` to `true`

#### Change xp rates

You'll need to edit the `worldprops`/`default.conf` directory file and set the `default_xp_rate = 1.0` to
`default_xp_rate = desired rate`

</details>

---

[license-shield]: https://img.shields.io/badge/license-AGPL--3.0-informational

[license-url]: https://www.gnu.org/licenses/agpl-3.0.en.html

[fork-shield]: https://img.shields.io/badge/repository-fork-blue

[fork-url]: https://gitlab.com/2009scape/2009scape

[libs]: https://img.shields.io/badge/constants-library-blue

[libs-url]: https://gitlab.com/rs2-emu/530-variables

[play-release]: https://img.shields.io/badge/singleplayer-release-blue

[play-url]: https://github.com/szumaster1/530-game