package com.joshuac.beatmatrix;

import java.io.IOException;
import java.io.InputStream;

import com.joshuac.beatmatrix.WavReader.WavException;

public abstract class AudioReader {
	public static class FileInfo {
		//Holds information needed for AudioTrack
		public int rate;
		public int channels;
		public int dataSize;

		public FileInfo(int rate, int channels, int dataSize) {
			this.rate = rate;
			this.channels = channels;
			this.dataSize = dataSize;
		}
	}
	
	public abstract FileInfo readHeader(InputStream wavStream) throws IOException,
	WavException;
		//Gives file info from file header
	
	public abstract byte[] readToPcm(FileInfo info, InputStream stream, int buffSize) throws IOException;
		//Returns pcm data from input stream

	public byte[] readToPcm(FileInfo info, InputStream stream) throws IOException {
		return readToPcm(info, stream, info.dataSize);
	}
}
