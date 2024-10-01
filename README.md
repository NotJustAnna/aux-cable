# Discord Aux Cable

![Screenshot of the Aux Cable app](https://github.com/NotJustAnna/aux-cable/blob/main/screenshots/VoiceChannelScreen.selector.png?raw=true)

"Aux Cable" is a self-hosted Discord "bot" that allows you to stream any input from your computer to a voice channel on Discord.
This is useful for listening to music together, DJing, TTRPG sound effects, or any other audio you want to share with your friends.

It's a simple Swing application that uses the Discord API (with [JDA](https://github.com/discord-jda/JDA) as the special sauce)
to connect a bot to a voice channel and stream audio from your computer's input device (using Java's Sound API).

## Recommended setup

1. **Create a Discord bot**:
   - Go to the [Discord Developer Portal](https://discord.com/developers/applications)
   - Create a new application
   - Go to the "Bot" tab and create a bot
   - Copy the bot token
       - You will need this token to run the application.
       - **Do not share this token with anyone!** It's like a password for your bot.
2. **Invite the bot to your server**:
   - Go to the "OAuth2" tab
   - Select the "bot" scope
   - Select the "Connect" and "Speak" permissions
   - Copy the generated URL and open it in your browser
   - Select the server you want to invite the bot to
3. **Install any sort of virtual audio cable software**:
   - On Windows, I recommend [any of the VoiceMeeter editions](https://vb-audio.com/Voicemeeter/)
4. **Install Java**:
   - The release version was built with Java 21. If you need to install it, here's a few options:
     - [Amazon Corretto](https://aws.amazon.com/corretto/), Amazon's Java distribution.
     - [Adoptium](https://adoptium.net/), a distribution of Java by the Eclipse Foundation.
     - Want even more choices? [The Java Almanac lists them all.](https://javaalmanac.io/jdk/21/#downloads:~:text=Downloads,-Vendor)
5. **Download the latest release**:
   - Go to the [releases page](https://github.com/NotJustAnna/aux-cable/releases) and download any of the files.
     - `aux-cable-*.*-app.jar` is the standalone JAR file.
       - This is the simplest way to run the application, just double-click the file or run `java -jar` followed by the file name.
     - `aux-cable-*.*-app.zip` and `aux-cable-*.*-app.tar` are bundled with start scripts that runs the JAR with recommended JVM options.
       - This is the recommended way to run the application.
       - Just download one of the archives, extract it, and run the start script inside the `bin` directory.
6. **Running the application**:
   - Paste the token into the "Bot token" field and click "Login".
   - Select the guild and voice channel you want to stream to and click "Connect".
   - Select the input device you want to stream and unmute the bot.
   - Done! You should now be streaming audio to the voice channel.
   - Keep the application running while you want to stream audio. Closing the application will stop the stream and disconnect the bot.

## Building from source

As long as you have Java 21 installed, you can simply run `./gradlew build` (or `.\gradlew.bat build` on Windows)
to build the application. The JAR file will be located in `build/libs` and the distribution archives will be located in `build/distributions`.
