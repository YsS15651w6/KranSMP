require('dotenv').config(); // Load environment variables from .env
const exaroton = require('exaroton');
const mime = require('mime-types');
const inquirer = require('inquirer');
const fs = require('fs');
const path = require('path');
const readline = require('readline');
const enc = require('./enc'); // Ensure enc.js is properly implemented

const API_TOKEN = process.env.EXAROTON_API_TOKEN; // Store the API token in a .env file
const SERVER_ID = process.env.EXAROTON_SERVER_ID; // Store the server ID in a .env file

// Allowed MIME types for media files
const ALLOWED_MIME_TYPES = ['image/jpeg', 'image/png', 'video/mp4', 'video/webm'];

async function promptForPassword() {
  let authorized = false;

  while (!authorized) {
    // Prompt for password
    const passwordInput = await inquirer.prompt([
      {
        type: 'password',
        name: 'password',
        message: 'Enter your member password (cAsE sEnSiTiVe): ',
      },
    ]);

    const hash = enc.hash(passwordInput.password);
    authorized = await enc.authorize(hash);

    if (!authorized) {
      console.error('Unauthorized access! Please try again.');
    }
  }

  return authorized;
}

async function main() {
  console.log("Welcome to the Media Uploader!");

  // Ensure authorization first
  await promptForPassword();

  // Prompt for file path
  const fileInput = await inquirer.prompt([
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

  const filePath = fileInput.filePath;
  const fileType = mime.lookup(filePath);

  // Validate file type
  if (!ALLOWED_MIME_TYPES.includes(fileType)) {
    console.error(`Invalid file type! Allowed types: ${ALLOWED_MIME_TYPES.join(', ')}`);
    process.exit(1);
  }

  // Initialize Exaroton client
  const client = new exaroton.Client(API_TOKEN);
  const server = client.server(SERVER_ID);

  const fileName = path.basename(filePath);
  const destinationPath = `/media/${fileName}`; // Target directory on the server

  console.log(`Uploading ${fileName} to server (${destinationPath})...`);

  try {
    // Create a file object for the target location
    const file = server.getFile(destinationPath);

    // Upload the file to the specified directory on the server
    await file.upload(filePath);

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
  console.error('Unexpected error:', error.message);
  process.exit(1);
});
