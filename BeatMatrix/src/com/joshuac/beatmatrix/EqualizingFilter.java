package com.joshuac.beatmatrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EqualizingFilter {
	private double ROOT2 = Math.sqrt(2);
	private double fs;
	private int channels;
	ByteBuffer bb = ByteBuffer.allocate(2);
	
	private double Fbass = 400;	//Center frequency
	private double Kbass;
	private double Vbass = 1;	//Gain
	private boolean bassCut = false, bassBoost = false;
	private double[] aCoeffsBass = new double[3];
	private double[] bCoeffsBass = new double[3];
	private int[] xlastBassL = {0,0};
	private int[] xlastBassR = {0,0};
	private int[] ylastBassL = {0,0};
	private int[] ylastBassR = {0,0};
	
	private double Ftreble = 2600;	//Center frequency
	private double Ktreble;
	private double Vtreble = 1;	//Gain
	private boolean trebleCut = false, trebleBoost = false;
	private double[] aCoeffsTreble = new double[3];
	private double[] bCoeffsTreble = new double[3];
	private int[] xlastTrebleL = {0,0};
	private int[] xlastTrebleR = {0,0};
	private int[] ylastTrebleL = {0,0};
	private int[] ylastTrebleR = {0,0};
	
	public EqualizingFilter(double freq, int chan) {
		bb.order(ByteOrder.LITTLE_ENDIAN);
		channels = chan;
		fs = freq;
		calculateKbass();
		calculateKtreble();
		calculateCoeffs();
	}

	private void calculateKbass() {
		Kbass = Math.tan((Math.PI * Fbass)/fs);
	}

	private void calculateKtreble() {
		Ktreble = Math.tan((Math.PI * Ftreble)/fs);
	}
	
	private void calculateCoeffs() {
		calculateCoeffs(0);
	}
	
	private void calculateCoeffs(int mode) {
		double b0, b1, b2, a1, a2;
		if (mode == 1 || mode == 0) {
			//bass
			if (bassBoost) {
				//bass boost
				double div = (1 + ROOT2*Kbass + Math.pow(Kbass, 2));
				b0 = (1 + Math.sqrt(Vbass)*ROOT2*Kbass + Vbass*Math.pow(Kbass, 2)) / div;
				b1 = (2 * (Vbass*Math.pow(Kbass, 2) - 1) ) / div;
				b2 = (1 - Math.sqrt(Vbass)*ROOT2*Kbass + Vbass*Math.pow(Kbass, 2)) / div;
				a1 = (2 * (Math.pow(Kbass, 2) - 1) ) / div;
				a2 = (1 - ROOT2*Kbass + Math.pow(Kbass, 2)) / div;
			}
			else if (bassCut) {
				//bass cut
				double div = (1 + ROOT2*Math.sqrt(Vbass)*Kbass + Vbass*Math.pow(Kbass, 2));
				b0 = (1 + ROOT2*Kbass + Math.pow(Kbass, 2)) / div;
				b1 = (2 * (Math.pow(Kbass, 2) - 1) ) / div;
				b2 = (1 - ROOT2*Kbass + Math.pow(Kbass, 2)) / div;
				a1 = (2 * (Vbass*Math.pow(Kbass, 2) - 1) ) / div;
				a2 = (1 - ROOT2*Math.sqrt(Vbass)*Kbass + Vbass*Math.pow(Kbass, 2)) / div;
			}
			else {
				b0 = 1;
				b1 = 0;
				b2 = 0;
				a1 = 0;
				a2 = 0;
			}
			aCoeffsBass[0] = 1;
			aCoeffsBass[1] = a1;
			aCoeffsBass[2] = a2;
	
			bCoeffsBass[0] = b0;
			bCoeffsBass[1] = b1;
			bCoeffsBass[2] = b2;
			
//			aCoeffsBass[0] = 0;
//			aCoeffsBass[1] = 0;
//			aCoeffsBass[2] = 0;
//	
//			bCoeffsBass[0] = 1;
//			bCoeffsBass[1] = 0;
//			bCoeffsBass[2] = 0;
		}
		
		if (mode == 2 || mode == 0) {
			//treble
			if (trebleBoost) {
				//treble boost
				double div = (1 + ROOT2*Ktreble + Math.pow(Ktreble, 2));
				b0 = (Vtreble + ROOT2*Math.sqrt(Vtreble)*Ktreble + Math.pow(Ktreble, 2)) / div;
				b1 = (2 * (Math.pow(Ktreble, 2) - Vtreble) ) / div;
				b2 = (Vtreble - ROOT2*Math.sqrt(Vtreble)*Ktreble + Math.pow(Ktreble, 2)) / div;
				a1 = (2 * (Math.pow(Ktreble, 2) - 1) ) / div;
				a2 = (1 - ROOT2*Ktreble + Math.pow(Ktreble, 2)) / div;
			}
			else if (trebleCut) {
				//treble cut
				double div = (Vtreble + ROOT2*Math.sqrt(Vtreble)*Ktreble + Math.pow(Ktreble, 2));
				b0 = (1 + ROOT2*Ktreble + Math.pow(Ktreble, 2)) / div;
				b1 = (2 * (Math.pow(Kbass, 2) - 1) ) / div;
				b2 = (1 - ROOT2*Ktreble + Math.pow(Ktreble, 2)) / div;
				a1 = Vtreble * (2 * (Math.pow(Ktreble, 2)/Vtreble - 1) ) / div;
				a2 = Vtreble * (1 - ROOT2/Math.sqrt(Vtreble)*Ktreble + Math.pow(Ktreble, 2)/Vtreble) / div;
			}
			else {
				b0 = 1;
				b1 = 0;
				b2 = 0;
				a1 = 0;
				a2 = 0;
			}
			aCoeffsTreble[0] = 1;
			aCoeffsTreble[1] = a1;
			aCoeffsTreble[2] = a2;
	
			bCoeffsTreble[0] = b0;
			bCoeffsTreble[1] = b1;
			bCoeffsTreble[2] = b2;
			
//			aCoeffsTreble[0] = 0;
//			aCoeffsTreble[1] = 0;
//			aCoeffsTreble[2] = 0;
//	
//			bCoeffsTreble[0] = 1;
//			bCoeffsTreble[1] = 0;
//			bCoeffsTreble[2] = 0;
		}
	}
	
	public byte[] filter(byte[] input) {
		byte[] output = new byte[input.length];
		if (channels == 1) {
			//mono
			for (int i = 0; i < input.length; i += 2) {
				//System.out.println("Mono i = " + i);
				output[i] = input[i];
				output[i+1] = input[i+1];
				
				if (bassBoost || bassCut) {
					bb.put(0,output[i]);
					bb.put(1,output[i+1]);
					short xi = bb.getShort(0);
					short yi = (short) (-aCoeffsBass[1]*ylastBassL[0] - aCoeffsBass[2]*ylastBassL[1]
							+ bCoeffsBass[0]*xi + bCoeffsBass[1]*xlastBassL[0] +
							bCoeffsBass[2]*xlastBassL[1]);
					//if(dumbvar) System.out.println("[" + lyi + "," + ylastBassL[0] + "," + ylastBassL[1] + "], ["
					//		+ xi + "," + xlastBassL[0] + "," + xlastBassL[1] + "]");
					ylastBassL[1] = ylastBassL[0];
					xlastBassL[1] = xlastBassL[0];
					ylastBassL[0] = yi;
					xlastBassL[0] = xi;
					//lyi = Math.min(lyi, Short.MAX_VALUE);
					//lyi = Math.max(lyi, Short.MIN_VALUE);
					bb.putShort(0,yi);
					output[i] = bb.get(0);
					output[i+1] = bb.get(1);
				}
				
				if (trebleBoost || trebleCut) {
					bb.put(0,output[i]);
					bb.put(1,output[i+1]);
					short xi = bb.getShort(0);
					short yi = (short) (-aCoeffsTreble[1]*ylastTrebleL[0] - aCoeffsTreble[2]*ylastTrebleL[1]
							+ bCoeffsTreble[0]*xi + bCoeffsTreble[1]*xlastTrebleL[0] +
							bCoeffsTreble[2]*xlastTrebleL[1]);
					ylastTrebleL[1] = ylastTrebleL[0];
					xlastTrebleL[1] = xlastTrebleL[0];
					ylastTrebleL[0] = yi;
					xlastTrebleL[0] = xi;
					bb.putShort(0,yi);
					output[i] = bb.get(0);
					output[i+1] = bb.get(1);
				}
			}
		}

		if (channels == 2) {
			//stereo
			for (int i = 0; i < input.length; i += 2) {
				//System.out.println("StereoL i = " + i);
				output[i] = input[i];
				output[i+1] = input[i+1];

				if (bassBoost || bassCut) {
					bb.put(0,output[i]);
					bb.put(1,output[i+1]);
					short xi = bb.getShort(0);
					short yi = (short) (-aCoeffsBass[1]*ylastBassL[0] - aCoeffsBass[2]*ylastBassL[1]
							+ bCoeffsBass[0]*xi + bCoeffsBass[1]*xlastBassL[0] +
							bCoeffsBass[2]*xlastBassL[1]);
					ylastBassL[1] = ylastBassL[0];
					xlastBassL[1] = xlastBassL[0];
					ylastBassL[0] = yi;
					xlastBassL[0] = xi;
					bb.putShort(0,yi);
					output[i] = bb.get(0);
					output[i+1] = bb.get(1);
				}

				if (trebleBoost || trebleCut) {
					bb.put(0,output[i]);
					bb.put(1,output[i+1]);
					short xi = bb.getShort(0);
					short yi = (short) (-aCoeffsTreble[1]*ylastTrebleL[0] - aCoeffsTreble[2]*ylastTrebleL[1]
							+ bCoeffsTreble[0]*xi + bCoeffsTreble[1]*xlastTrebleL[0] +
							bCoeffsTreble[2]*xlastTrebleL[1]);
					ylastTrebleL[1] = ylastTrebleL[0];
					xlastTrebleL[1] = xlastTrebleL[0];
					ylastTrebleL[0] = yi;
					xlastTrebleL[0] = xi;
					bb.putShort(0,yi);
					output[i] = bb.get(0);
					output[i+1] = bb.get(1);
				}

				i += 2;
				//System.out.println("StereoR i = " + i);

				output[i] = input[i];
				output[i+1] = input[i+1];

				if (bassBoost || bassCut) {
					bb.put(0,output[i]);
					bb.put(1,output[i+1]);
					short xi = bb.getShort(0);
					short yi = (short) (-aCoeffsBass[1]*ylastBassR[0] - aCoeffsBass[2]*ylastBassR[1]
							+ bCoeffsBass[0]*xi + bCoeffsBass[1]*xlastBassR[0] +
							bCoeffsBass[2]*xlastBassR[1]);
					ylastBassR[1] = ylastBassR[0];
					xlastBassR[1] = xlastBassR[0];
					ylastBassR[0] = yi;
					xlastBassR[0] = xi;
					bb.putShort(0,yi);
					output[i] = bb.get(0);
					output[i+1] = bb.get(1);
				}

				if (trebleBoost || trebleCut) {
					bb.put(0,output[i]);
					bb.put(1,output[i+1]);
					short xi = bb.getShort(0);
					short yi = (short) (-aCoeffsTreble[1]*ylastTrebleR[0] - aCoeffsTreble[2]*ylastTrebleR[1]
							+ bCoeffsTreble[0]*xi + bCoeffsTreble[1]*xlastTrebleR[0] +
							bCoeffsTreble[2]*xlastTrebleR[1]);
					ylastTrebleR[1] = ylastTrebleR[0];
					xlastTrebleR[1] = xlastTrebleR[0];
					ylastTrebleR[0] = yi;
					xlastTrebleR[0] = xi;
					bb.putShort(0,yi);
					output[i] = bb.get(0);
					output[i+1] = bb.get(1);
				}
			}

		}
		
		return output;
	}
	
	public void setBass(double Gbass) {
		Vbass = Math.pow(10, (Gbass/20));
		bassBoost = false;
		bassCut = false;
		if (Gbass < 0) {
			bassCut = true;
			Vbass = 1/Vbass;
		}
		else if (Gbass > 0) {
			bassBoost = true;
		}
		calculateCoeffs(1);
	}
	
	public void setTreble(double Gtreble) {
		Vtreble = Math.pow(10, (Gtreble/20));
		trebleBoost = false;
		trebleCut = false;
		if (Gtreble < 0) {
			trebleCut = true;
			Vtreble = 1/Vtreble;
		}
		else if (Gtreble > 0) {
			trebleBoost = true;
		}
		calculateCoeffs(2);
	}
}
