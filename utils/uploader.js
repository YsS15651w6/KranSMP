const exaroton = require('exaroton');
const mime = require('mime-types');
const inquirer = require('inquirer');
const fs = require('fs');
const path = require('path');
const readline = require('readline');

const API_TOKEN = ''; // Removed, as this is public
const SERVER_ID = ''; // Removed, as this is public

// Allowed MIME types for media files
const ALLOWED_MIME_TYPES = ['image/jpeg', 'image/png', 'video/mp4', 'video/webm'];

async function main() {
  console.log("Welcome to the Media Uploader!");

  // Ask for file path using inquirer.prompt() in the correct format
  const answers = await inquirer.prompt([
    {
      type: 'input',
      name: 'filePath',
      message: 'Enter the path to the media file:',
      validate: (input) => {
        if (!fs.existsSync(input)) return 'File does not exist!';
        return true;
      },
    },
  ]);

  const { filePath } = answers;

  const fileType = mime.lookup(filePath);

  if (!ALLOWED_MIME_TYPES.includes(fileType)) {
    console.error(`Invalid file type! Only the following are allowed: ${ALLOWED_MIME_TYPES.join(', ')}`);
    process.exit(1);
  }

  const client = new exaroton.Client(API_TOKEN);
  const server = client.server(SERVER_ID);

  const fileName = path.basename(filePath);
  const destinationPath = `media/${fileName}`;

  console.log(`Uploading ${fileName} to server...`);

  try {
    await server.uploadFile(filePath, destinationPath);
    console.log(`File uploaded successfully to ${destinationPath}`);
  } catch (error) {
    console.error('Error uploading file:', error.message);
    process.exit(1);
  }

  // Pause execution to keep the console open
  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
  });

  rl.question('Press Enter to exit...', () => {
    rl.close();
  });
}

main().catch((error) => {
  console.error('Unexpected error:', error);
  process.exit(1);
});
