# Multimedia-DCT-Compression-
Working of DCT and how it is used by standard compression algorithms like JPEG and MPEG. Implemented a DCT based coder-decoder for compressing an image and simulate decoding

Understanding the working of DCT and how it is used by standard compression algorithms like JPEG and MPEG. Specifically, implemented a DCT based coder-decoder for compressing an image and simulate decoding using the baseline mode as well as progressive modes of data delivery.

-  <b>Where the parameters are defined as :</b>

-  InputImage – is the image to input to your coder-decoder (you may assume a fixed size and format that will be described on the class website)


-  QuantizationLevel – a factor that will decrease/increase compression as explained below. This value will range from 0 to 7


-  DeliveryMode – an index ranging from 1, 2, 3. A 1 implies baseline delivery, a 2 implies progressive delivery using spectral selection, a 3 implies progressive delivery using successive bit approximation.


-  Latency – a variable in milliseconds, which will give a suggestive “sleep” time between data blocks during decoding. This parameter will be used to “simulate” low and high band width decoding to properly evaluate the simulation of your delivery modes.


-  <b>Here is how the decoding should proceed depending on the mode used.</b>
-  1) Sequential Mode<br>
Each image block is encoded in a single left-to-right, top-to-bottom scan. You may assume that each latency iteration pertains to ONE BLOCK. So the process progresses as –<br>
Decode data of first block and display … sleep<br>
Decode data of second block and display … sleep<br>


- 2) Progressive Mode – Spectral Selection<br>
The DC coefficients of every image blocks is decoded first and displayed. Next the first AC coefficients is added for all the blocks and decoded. This goes on till all the coefficients are added to the decoding process. You may assume that each latency iteration occurs after EVERY SPECIFIC DCT COFFICIENT for all blocks. So the process progresses as<br>
Decode all blocks using only DC coefficient (set rest to zero) … sleep<br>
Decode all blocks using only DC, AC1 coefficient …. Sleep<br>
Decode all blocks using only DC, AC1, AC2 coefficient …. Sleep<br>


- 3) Progressive Mode – Successive Bit Approximation<br>
All DC and AC coefficients of all image blocks are decoded first and displayed in a successive-bit manner. So you will decode all blocks using the all the DC and AC coefficients, but only using the first significant bit of all coefficients Next, you will decode all DC and AC coefficients using the first two significant bits of all coefficients and so on. You may assume that each latency iteration occurs at EACH SIGNIFICANT BIT usage.. So the process progresses as<br>
Decode all blocks using 1st significant bit of all coefficients … Sleep<br>
Decode all blocks using 1st , 2nd significant bit of all coefficients …. Sleep<br>
Decode all blocks using 1st , 2nd , 3rd significant bit of all coefficients …. Sleep<br>
