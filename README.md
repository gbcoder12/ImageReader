# ImageReader
Can "write to" and "read from" certain images depending on pixel colors.

Here's how the program progressed over the first two days:

	1) I came up with the idea of wanting to store text inside an image in a "secret" way.
	2) My first method was to use black and white pixels as "binary" digits (white = 0, black = 1) and to spread them out throughout the image so they weren't clumped in one spot
			of the image. This however, made the image VERY grainy if there was too much text for smaller images.
	3) Next, I needed to find a better way to store the message without it looking like the image was affected, so I developed this implementation:

		Since each pixel is represented by an RGB value, I decided to take the least significant bits of each red (3 bits), green (3 bits), and blue (2 bits) and represent a character using those 8 bits.

			For example, lets say we have a string "hi". In binary (from ASCII table) that would be 01101000 01101001. To store this in an image, we would take the upper-left pixel's rgb values, and store 'h' there by changing the red to be 'xxxxx011', green to be 'xxxxx010', and blue to be 'xxxxxx00'. Do the same with 'i', and also, to indicate the ending of the encrypted message, I add on the ASCII character 'ETX' with decimal value 3 represented in binary as 00000011. So now, the first three pixels are represented by the characters 'h', 'i', and 'ETX'.

		To read the message in the image, everything is just undone and added to a StringBuilder to construct the message for each pixel until the 'ETX' character is reached.

	So far, only PNG images seem to work for decrypting. I've tested PNG and JPG images for encrypting which seem to work? I haven't done enough research into how Java's BufferedImage class handles different formats to understand what to do, so only PNG extensions work for now.


Future plans:

	-  Add compatibility for different file formats
	-  Create an interface (cmd line or graphical) for ease of use
	-  Anything else I can think of...
