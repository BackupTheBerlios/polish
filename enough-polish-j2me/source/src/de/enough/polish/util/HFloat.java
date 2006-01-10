package de.enough.polish.util;

/*
 * Created on 27-Nov-2005 at 14:08:51.
 *
 * HFloat - floating point arithmetics for mobile devices
 *
 * Copyright (c) 2005 Horst Jaeger / Medienkonzepte GbR, Cologne, Germany
 *
 * This file is part of J2ME Polish.
 *
 * HFloat is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * HFloat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You can receive a copy of the GNU General Public License
 * if you write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please mail
 * hfloat@medienkonzepte.de for details.
 *
 */
 
/*
 * This class implements floating point arithmetics and some maths. HFloats can be constructed from int like in new HFloat(3) or from String like in new HFloat("-3.14E-2") which means -0.0314 . If you don't want to use the E-Syntax, type new HFloat("-0.0314") instead.
 * 
 * The first argument of each operation is the HFloat object itself. E.g. if you want to know what 2.3 * 4.7 is, type
 * System.out.println((new HFloat("2.3")).mlt(new HFloat("4.7")).toString()); this yields "1.08100000E1" which is
 * just another way of writing 10.81 . HFloat will always use the scientific output format - there's no way of telling it
 * to use a different one.
 * 
 * In case of any invalid operation, the result will be NaN (Not A Number) . We did not want to waste Memory on additinal
 * classes so we did not define any HFloat-Exceptions.
 * 
 * Because a HFloat may be invalid, it can't be cast to int - it can be cast to Integer instead, using the toInteger()
 * function. The result will be null if the HFloat is NAN.
 * 
 * Two HFloats x and y can be compared using the cmp-Function. The result of x.cmp(y) will be -1 if x < y, 0 if x == y
 * and +1 if x > y . You can give a tolerance value eps as well using x.softCmp(y, eps, true) or x.softCmp(y, eps, false).
 * Then x and y will be considered equal if they differ less than or no more than eps respectively. The cmp and softCmp
 * functions both yield Integer instead of int so the result can be null in case of NaNs.
 * 
 * There are lots of examples about how to use HFloats in the HFloat.java source file as well.
 */
public class HFloat extends Object{
 
//	Const

	  private static final long    LONG_MAX     =             1000000000000000000L;
	  private static final int     INTEGER_MAX  =                      1000000000 ;
	  private static final int     INTEGER_MIN  =                       100000001 ;
	  private static final int     E_INT  =                               9 ;
	  private static final int     E_LONG =                              18 ;
	  public  static final HFloat  PI    = new HFloat("3.1415926535897931");
	  public  static final HFloat  EUL   = new HFloat("2.7182818284590451");
	  public  static final HFloat  NaN   = new HFloat("NaN"               );

//	Member

	  private               int     mant                                    ;
	  private               int     expo                                    ;
	  private               boolean valid                                   ;
	  
//Konstruktoren

  public HFloat(){init(0, 0, true);}
  
  public HFloat(HFloat orig){init(orig.mant, orig.expo, orig.valid);}

  public HFloat(int wertA){init(wertA, 0, true);}

  public HFloat(int wertA, int expoA){init(wertA, expoA, true);}

  public HFloat(String text){
    if(text.toLowerCase().equals("nan")){
      init(0, 0, false);      
    }else{
      int[] hilf = format(text);
      init(hilf[0], hilf[1], true);
    }
  }
  
//Cast

  public Integer toInteger(){
    if(!this.valid) {
		return null;
	}
    int     retval = this.mant;
    boolean rund   = false;
    for(int n = 0; n < this.expo; n++) {
		retval *= 10;
	}
    for(int n = 0; n > this.expo; n--){
		if(retval % 10 != 0) {
			rund = true;
		}
		retval /= 10;
    }
    if((this.mant < 0) && rund) {
		retval--;
	}
    return new Integer(retval);
  }

  public String toString(){
    if(!this.valid) {
		return "NaN";
	}
    int expoA;
    int cmpI = cmp().intValue();
    String wertS;
    if(cmpI == -1) return "-" + neg().toString();
    if(cmpI ==  0) {
		expoA = 0;
	} else{
       expoA = -1;
       for(int wertA = abs(this.mant); wertA > 0; wertA /= 10) expoA++;
    }
    wertS = (new Integer(this.mant)).toString();
    for(int n = wertS.length(); n < E_INT; n++) {
		wertS += "0";
	}
    return wertS.substring(0, 1) + "." + wertS.substring(1, E_INT) + "E" + (new Integer(expoA + this.expo)).toString();
  }
  
//  Member (public)

  public HFloat get(){return new HFloat(this);}

  public void set(HFloat orig){init(orig.mant, orig.expo, orig.valid);}

  public HFloat add(HFloat arg){
    if(!this.valid || !arg.valid) {
		return NaN;
	}
    if(    cmp().intValue() == 0) return arg.get();
    if(arg.cmp().intValue() == 0) return get();
    int wertH    = this.mant;
    int argWertH = arg.mant;
    int expoH    = this.expo;
    int argExpoH = arg.expo;
    while(expoH < argExpoH){
      expoH++;
      wertH /= 10;
    }
    while(expoH > argExpoH){
      argExpoH++;
      argWertH /= 10;
    }
    int expoR = expoH;
    int wertR = wertH + argWertH;
    while(wertR > INTEGER_MAX){
      expoR++;
      wertR /= 10;
    }
    return new HFloat(wertR, expoR);
  }
  
  public HFloat add(int    arg){return add(new HFloat(arg));}
  public HFloat add(String arg){return add(new HFloat(arg));}

  public HFloat neg(){
    if(!this.valid) {
		return NaN;
	}
    return new HFloat(-this.mant, this.expo);
  }
  
  public HFloat sbt(HFloat arg){return add(arg.neg());}

  public HFloat sbt(int    arg){return sbt(new HFloat(arg));}
  public HFloat sbt(String arg){return sbt(new HFloat(arg));}

  public HFloat mlt(HFloat arg){
    if(!this.valid || !arg.valid) {
		return NaN;
	}
    int[] hilf  = format((long) this.mant * (long) arg.mant);
    return new HFloat(hilf[0], this.expo + arg.expo + hilf[1]);
  }
  
  public HFloat mlt(int    arg){return mlt(new HFloat(arg));}
  public HFloat mlt(String arg){return mlt(new HFloat(arg));}

  public HFloat inv(){
    if(!this.valid || (cmp().intValue() == 0)) {
		return NaN;
	}
    int[] hilf  = format(LONG_MAX / this.mant);
    return new HFloat(hilf[0], -this.expo - E_LONG + hilf[1]);
  }
    
  public HFloat div(HFloat arg){return mlt(arg.inv());}
  
  public HFloat div(int    arg){return div(new HFloat(arg));}
  public HFloat div(String arg){return div(new HFloat(arg));}

  public HFloat abs(){
    if(!this.valid) {
		return NaN;
	}
    if(this.mant < 0) {
		return new HFloat(-this.mant, this.expo);
	} else {
		return new HFloat( this.mant, this.expo);
	}
  }
  
  public Integer cmp(){
    if(!this.valid) {
		return null;
	}
    if     (this.mant == 0) {
		return new Integer( 0);
	} else {
		if(this.mant  > 0) {
			return new Integer( 1);
		} else {
			return new Integer(-1);
		}
	}
  }
  
  public Integer cmp(HFloat arg){
	  return sbt(arg).cmp();
  }
  public Integer cmp(int    arg){
	  return sbt(arg).cmp();
  }
  public Integer cmp(String arg){
	  return sbt(arg).cmp();
  }
  
  public Integer softCmp(HFloat tol, boolean strict){
    if(!this.valid || !tol.valid) return null;
    int cmpVal = abs().cmp(tol).intValue();
    if((cmpVal == -1) || (!strict && (cmpVal != 1))) return new Integer(0);
    return cmp();
  }
  
  public Integer softCmp(int    tol, boolean strict){return softCmp(new HFloat(tol), strict);}
  public Integer softCmp(String tol, boolean strict){return softCmp(new HFloat(tol), strict);}
  
  
  public Integer softCmp(HFloat arg, HFloat tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(HFloat arg, int    tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(HFloat arg, String tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(int    arg, HFloat tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(int    arg, int    tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(int    arg, String tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(String arg, HFloat tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(String arg, int    tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(String arg, String tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  
  public HFloat unFrac(){
    Integer i = toInteger();
    if(i == null) return NaN;
    return new HFloat(i.intValue());
  }

  public HFloat frac(){return sbt(unFrac());}

  public HFloat mod(HFloat arg){return div(arg).frac().mlt(arg);}
  public HFloat mod(int    arg){return div(arg).frac().mlt(arg);}
  public HFloat mod(String arg){return div(arg).frac().mlt(arg);}
  
  //Analysis
  
  public HFloat quad(){return mlt(this);}  
  
  public HFloat sqrt(){
    if(!this.valid) {
		return NaN;
	}
    int cmpInt = cmp().intValue();
    if(cmpInt == -1) {
		return neg().sqrt();
	}
    if(cmpInt ==  0) {
		return new HFloat(0);
	}
    HFloat altRet = new HFloat(2);
    HFloat ret    = new HFloat(1);
    HFloat arg    = get();
    int    mltMe  = 0;
    while(arg.cmp(1).intValue() == 1){
      ++mltMe;
      arg = arg.div(4);
    }
    for(;ret.cmp(altRet).intValue() == -1;){
    	altRet = ret.get();
    	ret    = (arg.add(ret.quad())).div(ret).div(2);
    }
    for(int mltLauf = 0; mltLauf < mltMe; ++mltLauf) {
		ret = ret.mlt(2);
	}
    return ret;
  }  
  
  public HFloat pow(HFloat arg){
    if(!this.valid) {
		return NaN;
	}
    if(cmp().intValue() == 0) {
		return new HFloat();
	}
    return ln().mlt(arg).exp();
  }
  
  public HFloat pow(int    arg){return pow(new HFloat(arg));}
  public HFloat pow(String arg){return pow(new HFloat(arg));}
  
  public HFloat exp(){
    if(!this.valid) {
		return NaN;
	}
    if(cmp().intValue() < 0) {
		return neg().exp().inv();
	}    
    HFloat summand  = new HFloat( 1);
    HFloat summe    = new HFloat( 0);
    HFloat altSumme = new HFloat(-1);
    int    n        = 0;
    while(summe.cmp(altSumme).intValue() != 0){
      altSumme.set(summe);
      summe   = summe.add(summand);
      summand = summand.mlt(this).div(n + 1);
      n++;
    }
    return summe;
  }
  
  public HFloat ln(){
    if(!this.valid) {
		return NaN;
	}
    int cmpInt = cmp().intValue();
    if(cmpInt ==  0) {
		return NaN;
	}
    if(cmpInt == -1) {
		return neg().ln();
	}
    HFloat xN      = this;
    HFloat zuKlein = new HFloat("0.5");
    HFloat zuGross = new HFloat("1.5");
    int addMe = 0;
    while(xN.cmp(zuKlein).intValue() == -1){
      --addMe;
      xN = xN.mlt(EUL);
    }
    while(xN.cmp(zuGross).intValue() == 1){
      ++addMe;
      xN = xN.div(EUL);
    }
    xN              = xN.sbt(1);
    HFloat x        = xN.neg();
    HFloat summe    = new HFloat(  0);
    HFloat altSumme = new HFloat( -1);
    int    n        = 1;  
    while(summe.cmp(altSumme).intValue() != 0){
      altSumme.set(summe);
      summe   = summe.add(xN.div(n));
      xN      = xN.mlt(x);
      ++n;
    }
    return summe.add(addMe);
  }

  public HFloat sin(){
    if(!this.valid) {
		return NaN;
	}
    HFloat arg      = mod(PI.mlt(2)).sbt(PI);
    HFloat argm2    = arg.quad().neg();
    HFloat summand  = new HFloat(arg);
    HFloat summe    = new HFloat(  0);
    HFloat altSumme = new HFloat( -1);
    int    n        = 0;  
    while(summe.cmp(altSumme).intValue() != 0){
      altSumme.set(summe);
      summe   = summe.add(summand);
      summand = summand.mlt(argm2).div((2 * n + 2) * (2 * n + 3));
      n++;
    }
    return summe.neg();
  }
  
  public HFloat cos(){
    if(!this.valid) {
		return NaN;
	}  
    return add(PI.div(2)).sin();

  }
  
  public HFloat tan(){
    if(!this.valid) {
		return NaN;
	}
    return sin().div(cos());
  }
  
  public HFloat cot(){
    if(!this.valid) {
		return NaN;
	}
    return cos().div(sin());
  }
  
  public HFloat asin(){
    if(!this.valid) {
		return NaN;
	}
    if(softCmp(0, 1, false).intValue() != 0) return NaN;
    HFloat og    = PI.div(2);
    HFloat ug    = og.neg();
    HFloat mi    = og.add(ug).div(2);
    HFloat altMi = mi.get();
    for(;;){
      if(cmp(mi.sin()).intValue() == 1) ug.set(mi);
      else                              og.set(mi);
      mi.set(og.add(ug).div(2));
      if(mi.cmp(altMi).intValue() == 0) return mi;
      altMi.set(mi);
    }
  }
  
  public HFloat acos(){
    if(!this.valid) {
		return NaN;
	}
    return PI.div(2).sbt(asin());
}
  
  public HFloat atan(){
    if(!this.valid) {
		return NaN;
	}
    if(cmp().intValue() == -1) {
		return neg().atan().neg();
	}
    HFloat ug    = new HFloat();
    HFloat og    = new HFloat(2);
    HFloat mi    = og.add(ug).div(2);
    HFloat altMi = mi.get();
    for(;;){
      HFloat tanVal = mi.tan();
      if(
        !tanVal.valid                ||
        tanVal.cmp().intValue() == -1 ||
        cmp(tanVal).intValue()  == -1
      )    og.set(mi);
      else ug.set(mi);
      mi.set(og.add(ug).div(2));
      if(mi.cmp(altMi).intValue() == 0) return mi;
      altMi.set(mi);
    }
  }
  
  public HFloat acot(){
    if(!this.valid) {
		return NaN;
	}
    return PI.div(2).sbt(atan());
  }

  public HFloat sinh(){return exp().sbt(neg().exp()).div(2);}
  
  public HFloat cosh(){return exp().add(neg().exp()).div(2);}
  
  public HFloat tanh(){return sinh().div(cosh());}
  
  public HFloat coth(){return cosh().div(sinh());}

  public HFloat asinh(){return add(quad().add(1).sqrt()).ln();}
  
  public HFloat acosh(){return add(quad().sbt(1).sqrt()).ln();}
  
  public HFloat atanh(){return (add(1).div(sbt(1))).ln().div(2);}
  
  public HFloat acoth(){return (add(1).div(sbt(1))).ln().div(2);}

  //Hilf (private)
  
  private void init(int wertA, int expoA, boolean validA){
    this.mant  = wertA;
    this.expo  = expoA;
    this.valid = validA;
    norm();
  }
  
  private int stringIndexOf(String arg, String pat){
    int retval = arg.indexOf(pat);
    return (retval < 0) ? arg.length() : retval;
  }
  
  private int[] format(long arg){
    int[] retval = new int[2];
    int   expoR  = 0;
    long  wertR  = arg;
    for(; abs(wertR) > INTEGER_MAX; wertR /= 10) expoR++;
    retval[0] = (int) wertR;
    retval[1] = expoR;
    return retval;
  }
  
  private int[] format(String text){
    int klEPos = stringIndexOf(text, "e");
    int grEPos = stringIndexOf(text, "E");
    int ptPos  = stringIndexOf(text, ".");
    int kmPos  = stringIndexOf(text, ",");
    int ePos   = (klEPos < grEPos) ? klEPos : grEPos;
    int pkPos  = (ptPos  < kmPos ) ? ptPos  : kmPos ;
    if(ePos < pkPos) pkPos = ePos;
    int[] retval = praeFormat(text.substring(0, ePos));
    retval[1]    = retval[1] - ePos + pkPos + praeFormat(text.substring(ePos))[0];
    return retval;
  }
  
  private int[] praeFormat(String arg){
    int[] retval  = new int[2];
    if(arg.equals("")){
      retval[0] = 0;
      retval[1] = 0;
      return retval;
    }
    char vorz = arg.charAt(0);
    if(vorz == '+' || vorz == ' ' || vorz == 'e' || vorz == 'E') return praeFormat(arg.substring(1));
    if(vorz == '-'               ){
      retval    = praeFormat(arg.substring(1));
      retval[0] = -retval[0];
      return retval;
    }
    String text = "";
    String num  = "0123456789";
    String pkS  = ".,";
    int    pkN  = 0;
    for(int n = 0; n < arg.length(); n++){
      char zch = arg.charAt(n);
      if(num.indexOf(zch) != -1) text += zch;
      if(pkS.indexOf(zch) != -1) pkN++;
    }
    int textLength = text.length();
    if(textLength > E_INT){
      retval[0] = Integer.parseInt(text.substring(0, E_INT));
      retval[1] = textLength - E_INT + pkN;
    }else{
      retval[0] = Integer.parseInt(text);
      retval[1] = pkN;
    }
    return retval;
  }
  
  private void norm(){
    if(this.mant == 0){
      this.expo = 0;
    }else{
      while(abs(this.mant) < INTEGER_MIN){
        this.expo--;
        this.mant *= 10;
      }
    }
  }
  
  private int abs(int i){
    if(i < 0) return -i;
    else      return  i;
  }
  
  private long abs(long i){
    if(i < 0) return -i;
    else      return  i;
  }
  

}
                                                                                                                              