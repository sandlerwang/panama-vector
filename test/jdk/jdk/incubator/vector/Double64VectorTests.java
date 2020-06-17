/*
 * Copyright (c) 2018, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @modules jdk.incubator.vector
 * @run testng/othervm -ea -esa -Xbatch Double64VectorTests
 */

// -- This file was mechanically generated: Do not edit! -- //

import jdk.incubator.vector.VectorShape;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorShuffle;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.Vector;

import jdk.incubator.vector.DoubleVector;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.Integer;
import java.util.List;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Test
public class Double64VectorTests extends AbstractVectorTest {

    static final VectorSpecies<Double> SPECIES =
                DoubleVector.SPECIES_64;

    static final int INVOC_COUNT = Integer.getInteger("jdk.incubator.vector.test.loop-iterations", 100);


    static final int BUFFER_REPS = Integer.getInteger("jdk.incubator.vector.test.buffer-vectors", 25000 / 64);

    static final int BUFFER_SIZE = Integer.getInteger("jdk.incubator.vector.test.buffer-size", BUFFER_REPS * (64 / 8));

    interface FUnOp {
        double apply(double a);
    }

    static void assertArraysEquals(double[] a, double[] r, FUnOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i]), "at index #" + i + ", input = " + a[i]);
        }
    }

    interface FUnArrayOp {
        double[] apply(double a);
    }

    static void assertArraysEquals(double[] a, double[] r, FUnArrayOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a[i]));
            }
        } catch (AssertionError e) {
            double[] ref = f.apply(a[i]);
            double[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i);
        }
    }

    static void assertArraysEquals(double[] a, double[] r, boolean[] mask, FUnOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? f.apply(a[i]) : a[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? f.apply(a[i]) : a[i], "at index #" + i + ", input = " + a[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    interface FReductionOp {
        double apply(double[] a, int idx);
    }

    interface FReductionAllOp {
        double apply(double[] a);
    }

    static void assertReductionArraysEquals(double[] a, double[] b, double c,
                                            FReductionOp f, FReductionAllOp fa) {
        int i = 0;
        try {
            Assert.assertEquals(c, fa.apply(a));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(b[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(c, fa.apply(a), "Final result is incorrect!");
            Assert.assertEquals(b[i], f.apply(a, i), "at index #" + i);
        }
    }

    interface FReductionMaskedOp {
        double apply(double[] a, int idx, boolean[] mask);
    }

    interface FReductionAllMaskedOp {
        double apply(double[] a, boolean[] mask);
    }

    static void assertReductionArraysEqualsMasked(double[] a, double[] b, double c, boolean[] mask,
                                            FReductionMaskedOp f, FReductionAllMaskedOp fa) {
        int i = 0;
        try {
            Assert.assertEquals(c, fa.apply(a, mask));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(b[i], f.apply(a, i, mask));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(c, fa.apply(a, mask), "Final result is incorrect!");
            Assert.assertEquals(b[i], f.apply(a, i, mask), "at index #" + i);
        }
    }

    interface FReductionOpLong {
        long apply(double[] a, int idx);
    }

    interface FReductionAllOpLong {
        long apply(double[] a);
    }

    static void assertReductionLongArraysEquals(double[] a, long[] b, long c,
                                            FReductionOpLong f, FReductionAllOpLong fa) {
        int i = 0;
        try {
            Assert.assertEquals(c, fa.apply(a));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(b[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(c, fa.apply(a), "Final result is incorrect!");
            Assert.assertEquals(b[i], f.apply(a, i), "at index #" + i);
        }
    }

    interface FReductionMaskedOpLong {
        long apply(double[] a, int idx, boolean[] mask);
    }

    interface FReductionAllMaskedOpLong {
        long apply(double[] a, boolean[] mask);
    }

    static void assertReductionLongArraysEqualsMasked(double[] a, long[] b, long c, boolean[] mask,
                                            FReductionMaskedOpLong f, FReductionAllMaskedOpLong fa) {
        int i = 0;
        try {
            Assert.assertEquals(c, fa.apply(a, mask));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(b[i], f.apply(a, i, mask));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(c, fa.apply(a, mask), "Final result is incorrect!");
            Assert.assertEquals(b[i], f.apply(a, i, mask), "at index #" + i);
        }
    }

    interface FBoolReductionOp {
        boolean apply(boolean[] a, int idx);
    }

    static void assertReductionBoolArraysEquals(boolean[] a, boolean[] b, FBoolReductionOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(b[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(b[i], f.apply(a, i), "at index #" + i);
        }
    }

    static void assertInsertArraysEquals(double[] a, double[] b, double element, int index) {
        int i = 0;
        try {
            for (; i < a.length; i += 1) {
                if(i%SPECIES.length() == index) {
                    Assert.assertEquals(b[i], element);
                } else {
                    Assert.assertEquals(b[i], a[i]);
                }
            }
        } catch (AssertionError e) {
            if (i%SPECIES.length() == index) {
                Assert.assertEquals(b[i], element, "at index #" + i);
            } else {
                Assert.assertEquals(b[i], a[i], "at index #" + i);
            }
        }
    }

    static void assertRearrangeArraysEquals(double[] a, double[] r, int[] order, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    Assert.assertEquals(r[i+j], a[i+order[i+j]]);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            Assert.assertEquals(r[i+j], a[i+order[i+j]], "at index #" + idx + ", input = " + a[i+order[i+j]]);
        }
    }

    static void assertSelectFromArraysEquals(double[] a, double[] r, double[] order, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    Assert.assertEquals(r[i+j], a[i+(int)order[i+j]]);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            Assert.assertEquals(r[i+j], a[i+(int)order[i+j]], "at index #" + idx + ", input = " + a[i+(int)order[i+j]]);
        }
    }

    static void assertRearrangeArraysEquals(double[] a, double[] r, int[] order, boolean[] mask, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    if (mask[j % SPECIES.length()])
                         Assert.assertEquals(r[i+j], a[i+order[i+j]]);
                    else
                         Assert.assertEquals(r[i+j], (double)0);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            if (mask[j % SPECIES.length()])
                Assert.assertEquals(r[i+j], a[i+order[i+j]], "at index #" + idx + ", input = " + a[i+order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
            else
                Assert.assertEquals(r[i+j], (double)0, "at index #" + idx + ", input = " + a[i+order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
        }
    }

    static void assertSelectFromArraysEquals(double[] a, double[] r, double[] order, boolean[] mask, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    if (mask[j % SPECIES.length()])
                         Assert.assertEquals(r[i+j], a[i+(int)order[i+j]]);
                    else
                         Assert.assertEquals(r[i+j], (double)0);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            if (mask[j % SPECIES.length()])
                Assert.assertEquals(r[i+j], a[i+(int)order[i+j]], "at index #" + idx + ", input = " + a[i+(int)order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
            else
                Assert.assertEquals(r[i+j], (double)0, "at index #" + idx + ", input = " + a[i+(int)order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(double[]a, double[]r) {
        int i = 0;
        for (; i < a.length; i += SPECIES.length()) {
            int idx = i;
            for (int j = idx; j < (idx + SPECIES.length()); j++)
                a[j]=a[idx];
        }

        try {
            for (i = 0; i < a.length; i++) {
                Assert.assertEquals(r[i], a[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], a[i], "at index #" + i + ", input = " + a[i]);
        }
    }

    interface FBinOp {
        double apply(double a, double b);
    }

    interface FBinMaskOp {
        double apply(double a, double b, boolean m);

        static FBinMaskOp lift(FBinOp f) {
            return (a, b, m) -> m ? f.apply(a, b) : a;
        }
    }

    static void assertArraysEquals(double[] a, double[] b, double[] r, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i]), "(" + a[i] + ", " + b[i] + ") at index #" + i);
        }
    }

    static void assertBroadcastArraysEquals(double[] a, double[] b, double[] r, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()]),
                                "(" + a[i] + ", " + b[(i / SPECIES.length()) * SPECIES.length()] + ") at index #" + i);
        }
    }


    static void assertArraysEquals(double[] a, double[] b, double[] r, boolean[] mask, FBinOp f) {
        assertArraysEquals(a, b, r, mask, FBinMaskOp.lift(f));
    }

    static void assertArraysEquals(double[] a, double[] b, double[] r, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(double[] a, double[] b, double[] r, boolean[] mask, FBinOp f) {
        assertBroadcastArraysEquals(a, b, r, mask, FBinMaskOp.lift(f));
    }

    static void assertBroadcastArraysEquals(double[] a, double[] b, double[] r, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] +
                                ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }


    static void assertShiftArraysEquals(double[] a, double[] b, double[] r, FBinOp f) {
        int i = 0;
        int j = 0;
        try {
            for (; j < a.length; j += SPECIES.length()) {
                for (i = 0; i < SPECIES.length(); i++) {
                    Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j]));
                }
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j]), "at index #" + i + ", " + j);
        }
    }

    static void assertShiftArraysEquals(double[] a, double[] b, double[] r, boolean[] mask, FBinOp f) {
        assertShiftArraysEquals(a, b, r, mask, FBinMaskOp.lift(f));
    }

    static void assertShiftArraysEquals(double[] a, double[] b, double[] r, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        int j = 0;
        try {
            for (; j < a.length; j += SPECIES.length()) {
                for (i = 0; i < SPECIES.length(); i++) {
                    Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j], mask[i]));
                }
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j], mask[i]), "at index #" + i + ", input1 = " + a[i+j] + ", input2 = " + b[j] + ", mask = " + mask[i]);
        }
    }

    interface FTernOp {
        double apply(double a, double b, double c);
    }

    interface FTernMaskOp {
        double apply(double a, double b, double c, boolean m);

        static FTernMaskOp lift(FTernOp f) {
            return (a, b, c, m) -> m ? f.apply(a, b, c) : a;
        }
    }

    static void assertArraysEquals(double[] a, double[] b, double[] c, double[] r, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " + c[i]);
        }
    }

    static void assertArraysEquals(double[] a, double[] b, double[] c, double[] r, boolean[] mask, FTernOp f) {
        assertArraysEquals(a, b, c, r, mask, FTernMaskOp.lift(f));
    }

    static void assertArraysEquals(double[] a, double[] b, double[] c, double[] r, boolean[] mask, FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i], mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = "
              + b[i] + ", input3 = " + c[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(double[] a, double[] b, double[] c, double[] r, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()]), "at index #" +
                                i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " +
                                c[(i / SPECIES.length()) * SPECIES.length()]);
        }
    }

    static void assertAltBroadcastArraysEquals(double[] a, double[] b, double[] c, double[] r, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i]), "at index #" +
                                i + ", input1 = " + a[i] + ", input2 = " +
                                b[(i / SPECIES.length()) * SPECIES.length()] + ",  input3 = " + c[i]);
        }
    }

    static void assertBroadcastArraysEquals(double[] a, double[] b, double[] c, double[] r, boolean[] mask,
                                            FTernOp f) {
        assertBroadcastArraysEquals(a, b, c, r, mask, FTernMaskOp.lift(f));
    }

    static void assertBroadcastArraysEquals(double[] a, double[] b, double[] c, double[] r, boolean[] mask,
                                            FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()],
                                    mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " +
                                b[i] + ", input3 = " + c[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }

    static void assertAltBroadcastArraysEquals(double[] a, double[] b, double[] c, double[] r, boolean[] mask,
                                            FTernOp f) {
        assertAltBroadcastArraysEquals(a, b, c, r, mask, FTernMaskOp.lift(f));
    }

    static void assertAltBroadcastArraysEquals(double[] a, double[] b, double[] c, double[] r, boolean[] mask,
                                            FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i],
                                    mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] +
                                ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] +
                                ", input3 = " + c[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertDoubleBroadcastArraysEquals(double[] a, double[] b, double[] c, double[] r, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                    c[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                c[(i / SPECIES.length()) * SPECIES.length()]), "at index #" + i + ", input1 = " + a[i]
                                + ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] + ", input3 = " +
                                c[(i / SPECIES.length()) * SPECIES.length()]);
        }
    }

    static void assertDoubleBroadcastArraysEquals(double[] a, double[] b, double[] c, double[] r, boolean[] mask,
                                                  FTernOp f) {
        assertDoubleBroadcastArraysEquals(a, b, c, r, mask, FTernMaskOp.lift(f));
    }

    static void assertDoubleBroadcastArraysEquals(double[] a, double[] b, double[] c, double[] r, boolean[] mask,
                                                  FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                    c[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                c[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]), "at index #"
                                + i + ", input1 = " + a[i] + ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] +
                                ", input3 = " + c[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }


    static boolean isWithin1Ulp(double actual, double expected) {
        if (Double.isNaN(expected) && !Double.isNaN(actual)) {
            return false;
        } else if (!Double.isNaN(expected) && Double.isNaN(actual)) {
            return false;
        }

        double low = Math.nextDown(expected);
        double high = Math.nextUp(expected);

        if (Double.compare(low, expected) > 0) {
            return false;
        }

        if (Double.compare(high, expected) < 0) {
            return false;
        }

        return true;
    }

    static void assertArraysEqualsWithinOneUlp(double[] a, double[] r, FUnOp mathf, FUnOp strictmathf) {
        int i = 0;
        try {
            // Check that result is within 1 ulp of strict math or equivalent to math implementation.
            for (; i < a.length; i++) {
                Assert.assertTrue(Double.compare(r[i], mathf.apply(a[i])) == 0 ||
                                    isWithin1Ulp(r[i], strictmathf.apply(a[i])));
            }
        } catch (AssertionError e) {
            Assert.assertTrue(Double.compare(r[i], mathf.apply(a[i])) == 0, "at index #" + i + ", input = " + a[i] + ", actual = " + r[i] + ", expected = " + mathf.apply(a[i]));
            Assert.assertTrue(isWithin1Ulp(r[i], strictmathf.apply(a[i])), "at index #" + i + ", input = " + a[i] + ", actual = " + r[i] + ", expected (within 1 ulp) = " + strictmathf.apply(a[i]));
        }
    }

    static void assertArraysEqualsWithinOneUlp(double[] a, double[] b, double[] r, FBinOp mathf, FBinOp strictmathf) {
        int i = 0;
        try {
            // Check that result is within 1 ulp of strict math or equivalent to math implementation.
            for (; i < a.length; i++) {
                Assert.assertTrue(Double.compare(r[i], mathf.apply(a[i], b[i])) == 0 ||
                                    isWithin1Ulp(r[i], strictmathf.apply(a[i], b[i])));
            }
        } catch (AssertionError e) {
            Assert.assertTrue(Double.compare(r[i], mathf.apply(a[i], b[i])) == 0, "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", actual = " + r[i] + ", expected = " + mathf.apply(a[i], b[i]));
            Assert.assertTrue(isWithin1Ulp(r[i], strictmathf.apply(a[i], b[i])), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", actual = " + r[i] + ", expected (within 1 ulp) = " + strictmathf.apply(a[i], b[i]));
        }
    }

    static void assertBroadcastArraysEqualsWithinOneUlp(double[] a, double[] b, double[] r,
                                                        FBinOp mathf, FBinOp strictmathf) {
        int i = 0;
        try {
            // Check that result is within 1 ulp of strict math or equivalent to math implementation.
            for (; i < a.length; i++) {
                Assert.assertTrue(Double.compare(r[i],
                                  mathf.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()])) == 0 ||
                                  isWithin1Ulp(r[i],
                                  strictmathf.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()])));
            }
        } catch (AssertionError e) {
            Assert.assertTrue(Double.compare(r[i],
                              mathf.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()])) == 0,
                              "at index #" + i + ", input1 = " + a[i] + ", input2 = " +
                              b[(i / SPECIES.length()) * SPECIES.length()] + ", actual = " + r[i] +
                              ", expected = " + mathf.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()]));
            Assert.assertTrue(isWithin1Ulp(r[i],
                              strictmathf.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()])),
                             "at index #" + i + ", input1 = " + a[i] + ", input2 = " +
                             b[(i / SPECIES.length()) * SPECIES.length()] + ", actual = " + r[i] +
                             ", expected (within 1 ulp) = " + strictmathf.apply(a[i],
                             b[(i / SPECIES.length()) * SPECIES.length()]));
        }
    }

    interface FBinArrayOp {
        double apply(double[] a, int b);
    }

    static void assertArraysEquals(double[] a, double[] r, FBinArrayOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a,i), "at index #" + i);
        }
    }

    interface FGatherScatterOp {
        double[] apply(double[] a, int ix, int[] b, int iy);
    }

    static void assertArraysEquals(double[] a, int[] b, double[] r, FGatherScatterOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, i, b, i));
            }
        } catch (AssertionError e) {
            double[] ref = f.apply(a, i, b, i);
            double[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + " at index #" + i);
        }
    }

    interface FGatherMaskedOp {
        double[] apply(double[] a, int ix, boolean[] mask, int[] b, int iy);
    }

    interface FScatterMaskedOp {
        double[] apply(double[] r, double[] a, int ix, boolean[] mask, int[] b, int iy);
    }

    static void assertArraysEquals(double[] a, int[] b, double[] r, boolean[] mask, FGatherMaskedOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, i, mask, b, i));
            }
        } catch (AssertionError e) {
            double[] ref = f.apply(a, i, mask, b, i);
            double[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + ", mask: "
              + Arrays.toString(mask)
              + " at index #" + i);
        }
    }

    static void assertArraysEquals(double[] a, int[] b, double[] r, boolean[] mask, FScatterMaskedOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(r, a, i, mask, b, i));
            }
        } catch (AssertionError e) {
            double[] ref = f.apply(r, a, i, mask, b, i);
            double[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + ", r: "
              + Arrays.toString(Arrays.copyOfRange(r, i, i+SPECIES.length()))
              + ", mask: "
              + Arrays.toString(mask)
              + " at index #" + i);
        }
    }

    interface FLaneOp {
        double[] apply(double[] a, int origin, int idx);
    }

    static void assertArraysEquals(double[] a, double[] r, int origin, FLaneOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, origin, i));
            }
        } catch (AssertionError e) {
            double[] ref = f.apply(a, origin, i);
            double[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i);
        }
    }

    interface FLaneBop {
        double[] apply(double[] a, double[] b, int origin, int idx);
    }

    static void assertArraysEquals(double[] a, double[] b, double[] r, int origin, FLaneBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, i));
            }
        } catch (AssertionError e) {
            double[] ref = f.apply(a, b, origin, i);
            double[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin);
        }
    }

    interface FLaneMaskedBop {
        double[] apply(double[] a, double[] b, int origin, boolean[] mask, int idx);
    }

    static void assertArraysEquals(double[] a, double[] b, double[] r, int origin, boolean[] mask, FLaneMaskedBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, mask, i));
            }
        } catch (AssertionError e) {
            double[] ref = f.apply(a, b, origin, mask, i);
            double[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin);
        }
    }

    interface FLanePartBop {
        double[] apply(double[] a, double[] b, int origin, int part, int idx);
    }

    static void assertArraysEquals(double[] a, double[] b, double[] r, int origin, int part, FLanePartBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, part, i));
            }
        } catch (AssertionError e) {
            double[] ref = f.apply(a, b, origin, part, i);
            double[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin
              + ", with part #" + part);
        }
    }

    interface FLanePartMaskedBop {
        double[] apply(double[] a, double[] b, int origin, int part, boolean[] mask, int idx);
    }

    static void assertArraysEquals(double[] a, double[] b, double[] r, int origin, int part, boolean[] mask, FLanePartMaskedBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, part, mask, i));
            }
        } catch (AssertionError e) {
            double[] ref = f.apply(a, b, origin, part, mask, i);
            double[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin
              + ", with part #" + part);
        }
    }

    static long bits(double e) {
        return  Double.doubleToLongBits(e);
    }

    static final List<IntFunction<double[]>> DOUBLE_GENERATORS = List.of(
            withToString("double[-i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (double)(-i * 5));
            }),
            withToString("double[i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (double)(i * 5));
            }),
            withToString("double[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (((double)(i + 1) == 0) ? 1 : (double)(i + 1)));
            }),
            withToString("double[cornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> cornerCaseValue(i));
            })
    );

    // Create combinations of pairs
    // @@@ Might be sensitive to order e.g. div by 0
    static final List<List<IntFunction<double[]>>> DOUBLE_GENERATOR_PAIRS =
        Stream.of(DOUBLE_GENERATORS.get(0)).
                flatMap(fa -> DOUBLE_GENERATORS.stream().skip(1).map(fb -> List.of(fa, fb))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] boolUnaryOpProvider() {
        return BOOL_ARRAY_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    static final List<List<IntFunction<double[]>>> DOUBLE_GENERATOR_TRIPLES =
        DOUBLE_GENERATOR_PAIRS.stream().
                flatMap(pair -> DOUBLE_GENERATORS.stream().map(f -> List.of(pair.get(0), pair.get(1), f))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] doubleBinaryOpProvider() {
        return DOUBLE_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] doubleIndexedOpProvider() {
        return DOUBLE_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] doubleBinaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> DOUBLE_GENERATOR_PAIRS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] doubleTernaryOpProvider() {
        return DOUBLE_GENERATOR_TRIPLES.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] doubleTernaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> DOUBLE_GENERATOR_TRIPLES.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] doubleUnaryOpProvider() {
        return DOUBLE_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] doubleUnaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> DOUBLE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] doubleUnaryOpShuffleProvider() {
        return INT_SHUFFLE_GENERATORS.stream().
                flatMap(fs -> DOUBLE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] doubleUnaryOpShuffleMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> INT_SHUFFLE_GENERATORS.stream().
                    flatMap(fs -> DOUBLE_GENERATORS.stream().map(fa -> {
                        return new Object[] {fa, fs, fm};
                }))).
                toArray(Object[][]::new);
    }

    static final List<BiFunction<Integer,Integer,double[]>> DOUBLE_SHUFFLE_GENERATORS = List.of(
            withToStringBi("shuffle[random]", (Integer l, Integer m) -> {
                double[] a = new double[l];
                for (int i = 0; i < 1; i++) {
                    a[i] = (double)RAND.nextInt(m);
                }
                return a;
            })
    );

    @DataProvider
    public Object[][] doubleUnaryOpSelectFromProvider() {
        return DOUBLE_SHUFFLE_GENERATORS.stream().
                flatMap(fs -> DOUBLE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] doubleUnaryOpSelectFromMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> DOUBLE_SHUFFLE_GENERATORS.stream().
                    flatMap(fs -> DOUBLE_GENERATORS.stream().map(fa -> {
                        return new Object[] {fa, fs, fm};
                }))).
                toArray(Object[][]::new);
    }


    @DataProvider
    public Object[][] doubleUnaryOpIndexProvider() {
        return INT_INDEX_GENERATORS.stream().
                flatMap(fs -> DOUBLE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] doubleUnaryMaskedOpIndexProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
          flatMap(fs -> INT_INDEX_GENERATORS.stream().flatMap(fm ->
            DOUBLE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm, fs};
            }))).
            toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] scatterMaskedOpIndexProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
          flatMap(fs -> INT_INDEX_GENERATORS.stream().flatMap(fm ->
            DOUBLE_GENERATORS.stream().flatMap(fn ->
              DOUBLE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fn, fm, fs};
            })))).
            toArray(Object[][]::new);
    }

    static final List<IntFunction<double[]>> DOUBLE_COMPARE_GENERATORS = List.of(
            withToString("double[i]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (double)i);
            }),
            withToString("double[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (double)(i + 1));
            }),
            withToString("double[i - 2]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (double)(i - 2));
            }),
            withToString("double[zigZag(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> i%3 == 0 ? (double)i : (i%3 == 1 ? (double)(i + 1) : (double)(i - 2)));
            }),
            withToString("double[cornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> cornerCaseValue(i));
            })
    );

    static final List<List<IntFunction<double[]>>> DOUBLE_TEST_GENERATOR_ARGS =
        DOUBLE_COMPARE_GENERATORS.stream().
                map(fa -> List.of(fa)).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] doubleTestOpProvider() {
        return DOUBLE_TEST_GENERATOR_ARGS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    static final List<List<IntFunction<double[]>>> DOUBLE_COMPARE_GENERATOR_PAIRS =
        DOUBLE_COMPARE_GENERATORS.stream().
                flatMap(fa -> DOUBLE_COMPARE_GENERATORS.stream().map(fb -> List.of(fa, fb))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] doubleCompareOpProvider() {
        return DOUBLE_COMPARE_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] doubleCompareOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> DOUBLE_COMPARE_GENERATOR_PAIRS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    interface ToDoubleF {
        double apply(int i);
    }

    static double[] fill(int s , ToDoubleF f) {
        return fill(new double[s], f);
    }

    static double[] fill(double[] a, ToDoubleF f) {
        for (int i = 0; i < a.length; i++) {
            a[i] = f.apply(i);
        }
        return a;
    }

    static double cornerCaseValue(int i) {
        switch(i % 7) {
            case 0:
                return Double.MAX_VALUE;
            case 1:
                return Double.MIN_VALUE;
            case 2:
                return Double.NEGATIVE_INFINITY;
            case 3:
                return Double.POSITIVE_INFINITY;
            case 4:
                return Double.NaN;
            case 5:
                return (double)0.0;
            default:
                return (double)-0.0;
        }
    }
    static double get(double[] a, int i) {
        return (double) a[i];
    }

    static final IntFunction<double[]> fr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new double[length];
    };

    static final IntFunction<boolean[]> fmr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new boolean[length];
    };

    static final IntFunction<long[]> lfr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new long[length];
    };


    @Test
    static void smokeTest1() {
        DoubleVector three = DoubleVector.broadcast(SPECIES, (byte)-3);
        DoubleVector three2 = (DoubleVector) SPECIES.broadcast(-3);
        assert(three.eq(three2).allTrue());
        DoubleVector three3 = three2.broadcast(1).broadcast(-3);
        assert(three.eq(three3).allTrue());
        int scale = 2;
        Class<?> ETYPE = double.class;
        if (ETYPE == double.class || ETYPE == long.class)
            scale = 1000000;
        else if (ETYPE == byte.class && SPECIES.length() >= 64)
            scale = 1;
        DoubleVector higher = three.addIndex(scale);
        VectorMask<Double> m = three.compare(VectorOperators.LE, higher);
        assert(m.allTrue());
        m = higher.min((double)-1).test(VectorOperators.IS_NEGATIVE);
        assert(m.allTrue());
        m = higher.test(VectorOperators.IS_FINITE);
        assert(m.allTrue());
        double max = higher.reduceLanes(VectorOperators.MAX);
        assert(max == -3 + scale * (SPECIES.length()-1));
    }

    private static double[]
    bothToArray(DoubleVector a, DoubleVector b) {
        double[] r = new double[a.length() + b.length()];
        a.intoArray(r, 0);
        b.intoArray(r, a.length());
        return r;
    }

    @Test
    static void smokeTest2() {
        // Do some zipping and shuffling.
        DoubleVector io = (DoubleVector) SPECIES.broadcast(0).addIndex(1);
        DoubleVector io2 = (DoubleVector) VectorShuffle.iota(SPECIES,0,1,false).toVector();
        Assert.assertEquals(io, io2);
        DoubleVector a = io.add((double)1); //[1,2]
        DoubleVector b = a.neg();  //[-1,-2]
        double[] abValues = bothToArray(a,b); //[1,2,-1,-2]
        VectorShuffle<Double> zip0 = VectorShuffle.makeZip(SPECIES, 0);
        VectorShuffle<Double> zip1 = VectorShuffle.makeZip(SPECIES, 1);
        DoubleVector zab0 = a.rearrange(zip0,b); //[1,-1]
        DoubleVector zab1 = a.rearrange(zip1,b); //[2,-2]
        double[] zabValues = bothToArray(zab0, zab1); //[1,-1,2,-2]
        // manually zip
        double[] manual = new double[zabValues.length];
        for (int i = 0; i < manual.length; i += 2) {
            manual[i+0] = abValues[i/2];
            manual[i+1] = abValues[a.length() + i/2];
        }
        Assert.assertEquals(Arrays.toString(zabValues), Arrays.toString(manual));
        VectorShuffle<Double> unz0 = VectorShuffle.makeUnzip(SPECIES, 0);
        VectorShuffle<Double> unz1 = VectorShuffle.makeUnzip(SPECIES, 1);
        DoubleVector uab0 = zab0.rearrange(unz0,zab1);
        DoubleVector uab1 = zab0.rearrange(unz1,zab1);
        double[] abValues1 = bothToArray(uab0, uab1);
        Assert.assertEquals(Arrays.toString(abValues), Arrays.toString(abValues1));
    }

    static void iotaShuffle() {
        DoubleVector io = (DoubleVector) SPECIES.broadcast(0).addIndex(1);
        DoubleVector io2 = (DoubleVector) VectorShuffle.iota(SPECIES, 0 , 1, false).toVector();
        Assert.assertEquals(io, io2);
    }

    @Test
    // Test all shuffle related operations.
    static void shuffleTest() {
        // To test backend instructions, make sure that C2 is used.
        for (int loop = 0; loop < INVOC_COUNT * INVOC_COUNT; loop++) {
            iotaShuffle();
        }
    }

    @Test
    void viewAsIntegeralLanesTest() {
        Vector<?> asIntegral = SPECIES.zero().viewAsIntegralLanes();
        VectorSpecies<?> asIntegralSpecies = asIntegral.species();
        Assert.assertNotEquals(asIntegralSpecies.elementType(), SPECIES.elementType());
        Assert.assertEquals(asIntegralSpecies.vectorShape(), SPECIES.vectorShape());
        Assert.assertEquals(asIntegralSpecies.length(), SPECIES.length());
        Assert.assertEquals(asIntegral.viewAsFloatingLanes().species(), SPECIES);
    }

    @Test
    void viewAsFloatingLanesTest() {
        Vector<?> asFloating = SPECIES.zero().viewAsFloatingLanes();
        Assert.assertEquals(asFloating.species(), SPECIES);
    }

    static double ADD(double a, double b) {
        return (double)(a + b);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void ADDDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ADD, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Double64VectorTests::ADD);
    }
    static double add(double a, double b) {
        return (double)(a + b);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void addDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.add(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Double64VectorTests::add);
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void ADDDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ADD, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::ADD);
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void addDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.add(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::add);
    }
    static double SUB(double a, double b) {
        return (double)(a - b);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void SUBDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUB, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Double64VectorTests::SUB);
    }
    static double sub(double a, double b) {
        return (double)(a - b);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void subDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.sub(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Double64VectorTests::sub);
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void SUBDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUB, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::SUB);
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void subDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.sub(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::sub);
    }
    static double MUL(double a, double b) {
        return (double)(a * b);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void MULDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MUL, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Double64VectorTests::MUL);
    }
    static double mul(double a, double b) {
        return (double)(a * b);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void mulDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.mul(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Double64VectorTests::mul);
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void MULDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MUL, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::MUL);
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void mulDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.mul(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::mul);
    }

    static double DIV(double a, double b) {
        return (double)(a / b);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void DIVDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.DIV, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Double64VectorTests::DIV);
    }
    static double div(double a, double b) {
        return (double)(a / b);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void divDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.div(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Double64VectorTests::div);
    }



    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void DIVDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.DIV, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::DIV);
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void divDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.div(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::div);
    }



    static double FIRST_NONZERO(double a, double b) {
        return (double)(Double.doubleToLongBits(a)!=0?a:b);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void FIRST_NONZERODouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.FIRST_NONZERO, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Double64VectorTests::FIRST_NONZERO);
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void FIRST_NONZERODouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.FIRST_NONZERO, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::FIRST_NONZERO);
    }









    @Test(dataProvider = "doubleBinaryOpProvider")
    static void addDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.add(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Double64VectorTests::add);
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void addDouble64VectorTestsBroadcastMaskedSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.add(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Double64VectorTests::add);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void subDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.sub(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Double64VectorTests::sub);
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void subDouble64VectorTestsBroadcastMaskedSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.sub(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Double64VectorTests::sub);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void mulDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.mul(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Double64VectorTests::mul);
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void mulDouble64VectorTestsBroadcastMaskedSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.mul(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Double64VectorTests::mul);
    }


    @Test(dataProvider = "doubleBinaryOpProvider")
    static void divDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.div(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Double64VectorTests::div);
    }



    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void divDouble64VectorTestsBroadcastMaskedSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.div(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Double64VectorTests::div);
    }











































    static double MIN(double a, double b) {
        return (double)(Math.min(a, b));
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void MINDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MIN, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Double64VectorTests::MIN);
    }
    static double min(double a, double b) {
        return (double)(Math.min(a, b));
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void minDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.min(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Double64VectorTests::min);
    }
    static double MAX(double a, double b) {
        return (double)(Math.max(a, b));
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void MAXDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MAX, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Double64VectorTests::MAX);
    }
    static double max(double a, double b) {
        return (double)(Math.max(a, b));
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void maxDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.max(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Double64VectorTests::max);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void MINDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.MIN, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Double64VectorTests::MIN);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void minDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.min(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Double64VectorTests::min);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void MAXDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.MAX, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Double64VectorTests::MAX);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void maxDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.max(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Double64VectorTests::max);
    }












    static double ADD(double[] a, int idx) {
        double res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res += a[i];
        }

        return res;
    }

    static double ADD(double[] a) {
        double res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            double tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp += a[i + j];
            }
            res += tmp;
        }

        return res;
    }
    @Test(dataProvider = "doubleUnaryOpProvider")
    static void ADDDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        double ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.ADD);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                ra += av.reduceLanes(VectorOperators.ADD);
            }
        }

        assertReductionArraysEquals(a, r, ra, Double64VectorTests::ADD, Double64VectorTests::ADD);
    }
    static double ADDMasked(double[] a, int idx, boolean[] mask) {
        double res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res += a[i];
        }

        return res;
    }

    static double ADDMasked(double[] a, boolean[] mask) {
        double res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            double tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp += a[i + j];
            }
            res += tmp;
        }

        return res;
    }
    @Test(dataProvider = "doubleUnaryOpMaskProvider")
    static void ADDDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        double ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.ADD, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                ra += av.reduceLanes(VectorOperators.ADD, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Double64VectorTests::ADDMasked, Double64VectorTests::ADDMasked);
    }
    static double MUL(double[] a, int idx) {
        double res = 1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res *= a[i];
        }

        return res;
    }

    static double MUL(double[] a) {
        double res = 1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            double tmp = 1;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp *= a[i + j];
            }
            res *= tmp;
        }

        return res;
    }
    @Test(dataProvider = "doubleUnaryOpProvider")
    static void MULDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        double ra = 1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MUL);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                ra *= av.reduceLanes(VectorOperators.MUL);
            }
        }

        assertReductionArraysEquals(a, r, ra, Double64VectorTests::MUL, Double64VectorTests::MUL);
    }
    static double MULMasked(double[] a, int idx, boolean[] mask) {
        double res = 1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res *= a[i];
        }

        return res;
    }

    static double MULMasked(double[] a, boolean[] mask) {
        double res = 1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            double tmp = 1;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp *= a[i + j];
            }
            res *= tmp;
        }

        return res;
    }
    @Test(dataProvider = "doubleUnaryOpMaskProvider")
    static void MULDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        double ra = 1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MUL, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                ra *= av.reduceLanes(VectorOperators.MUL, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Double64VectorTests::MULMasked, Double64VectorTests::MULMasked);
    }
    static double MIN(double[] a, int idx) {
        double res = Double.POSITIVE_INFINITY;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (double)Math.min(res, a[i]);
        }

        return res;
    }

    static double MIN(double[] a) {
        double res = Double.POSITIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            res = (double)Math.min(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "doubleUnaryOpProvider")
    static void MINDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        double ra = Double.POSITIVE_INFINITY;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MIN);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Double.POSITIVE_INFINITY;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                ra = (double)Math.min(ra, av.reduceLanes(VectorOperators.MIN));
            }
        }

        assertReductionArraysEquals(a, r, ra, Double64VectorTests::MIN, Double64VectorTests::MIN);
    }
    static double MINMasked(double[] a, int idx, boolean[] mask) {
        double res = Double.POSITIVE_INFINITY;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res = (double)Math.min(res, a[i]);
        }

        return res;
    }

    static double MINMasked(double[] a, boolean[] mask) {
        double res = Double.POSITIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            if(mask[i % SPECIES.length()])
                res = (double)Math.min(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "doubleUnaryOpMaskProvider")
    static void MINDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        double ra = Double.POSITIVE_INFINITY;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MIN, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Double.POSITIVE_INFINITY;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                ra = (double)Math.min(ra, av.reduceLanes(VectorOperators.MIN, vmask));
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Double64VectorTests::MINMasked, Double64VectorTests::MINMasked);
    }
    static double MAX(double[] a, int idx) {
        double res = Double.NEGATIVE_INFINITY;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (double)Math.max(res, a[i]);
        }

        return res;
    }

    static double MAX(double[] a) {
        double res = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            res = (double)Math.max(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "doubleUnaryOpProvider")
    static void MAXDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        double ra = Double.NEGATIVE_INFINITY;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MAX);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                ra = (double)Math.max(ra, av.reduceLanes(VectorOperators.MAX));
            }
        }

        assertReductionArraysEquals(a, r, ra, Double64VectorTests::MAX, Double64VectorTests::MAX);
    }
    static double MAXMasked(double[] a, int idx, boolean[] mask) {
        double res = Double.NEGATIVE_INFINITY;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res = (double)Math.max(res, a[i]);
        }

        return res;
    }

    static double MAXMasked(double[] a, boolean[] mask) {
        double res = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            if(mask[i % SPECIES.length()])
                res = (double)Math.max(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "doubleUnaryOpMaskProvider")
    static void MAXDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        double ra = Double.NEGATIVE_INFINITY;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MAX, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                ra = (double)Math.max(ra, av.reduceLanes(VectorOperators.MAX, vmask));
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Double64VectorTests::MAXMasked, Double64VectorTests::MAXMasked);
    }





    @Test(dataProvider = "doubleUnaryOpProvider")
    static void withDouble64VectorTests(IntFunction<double []> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.withLane(0, (double)4).intoArray(r, i);
            }
        }

        assertInsertArraysEquals(a, r, (double)4, 0);
    }
    static boolean testIS_DEFAULT(double a) {
        return bits(a)==0;
    }

    @Test(dataProvider = "doubleTestOpProvider")
    static void IS_DEFAULTDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                VectorMask<Double> mv = av.test(VectorOperators.IS_DEFAULT);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_DEFAULT(a[i + j]));
                }
            }
        }
    }

    static boolean testIS_NEGATIVE(double a) {
        return bits(a)<0;
    }

    @Test(dataProvider = "doubleTestOpProvider")
    static void IS_NEGATIVEDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                VectorMask<Double> mv = av.test(VectorOperators.IS_NEGATIVE);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_NEGATIVE(a[i + j]));
                }
            }
        }
    }


    static boolean testIS_FINITE(double a) {
        return Double.isFinite(a);
    }

    @Test(dataProvider = "doubleTestOpProvider")
    static void IS_FINITEDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                VectorMask<Double> mv = av.test(VectorOperators.IS_FINITE);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_FINITE(a[i + j]));
                }
            }
        }
    }



    static boolean testIS_NAN(double a) {
        return Double.isNaN(a);
    }

    @Test(dataProvider = "doubleTestOpProvider")
    static void IS_NANDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                VectorMask<Double> mv = av.test(VectorOperators.IS_NAN);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_NAN(a[i + j]));
                }
            }
        }
    }



    static boolean testIS_INFINITE(double a) {
        return Double.isInfinite(a);
    }

    @Test(dataProvider = "doubleTestOpProvider")
    static void IS_INFINITEDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                VectorMask<Double> mv = av.test(VectorOperators.IS_INFINITE);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_INFINITE(a[i + j]));
                }
            }
        }
    }



    @Test(dataProvider = "doubleCompareOpProvider")
    static void LTDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.LT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i + j]);
                }
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpProvider")
    static void ltDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.lt(bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "doubleCompareOpMaskProvider")
    static void LTDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                                IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.LT, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] < b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpProvider")
    static void GTDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.GT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] > b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "doubleCompareOpMaskProvider")
    static void GTDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                                IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.GT, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] > b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpProvider")
    static void EQDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.EQ, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i + j]);
                }
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpProvider")
    static void eqDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.eq(bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "doubleCompareOpMaskProvider")
    static void EQDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                                IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.EQ, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] == b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpProvider")
    static void NEDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.NE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] != b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "doubleCompareOpMaskProvider")
    static void NEDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                                IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.NE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] != b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpProvider")
    static void LEDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.LE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] <= b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "doubleCompareOpMaskProvider")
    static void LEDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                                IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.LE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] <= b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpProvider")
    static void GEDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.GE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] >= b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "doubleCompareOpMaskProvider")
    static void GEDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                                IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                VectorMask<Double> mv = av.compare(VectorOperators.GE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] >= b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpProvider")
    static void LTDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            VectorMask<Double> mv = av.compare(VectorOperators.LT, b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i]);
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpMaskProvider")
    static void LTDouble64VectorTestsBroadcastMaskedSmokeTest(IntFunction<double[]> fa,
                                IntFunction<double[]> fb, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            VectorMask<Double> mv = av.compare(VectorOperators.LT, b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] < b[i]));
            }
        }
    }

    @Test(dataProvider = "doubleCompareOpProvider")
    static void LTDouble64VectorTestsBroadcastLongSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            VectorMask<Double> mv = av.compare(VectorOperators.LT, (long)b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] < (double)((long)b[i]));
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpMaskProvider")
    static void LTDouble64VectorTestsBroadcastLongMaskedSmokeTest(IntFunction<double[]> fa,
                                IntFunction<double[]> fb, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            VectorMask<Double> mv = av.compare(VectorOperators.LT, (long)b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] < (double)((long)b[i])));
            }
        }
    }

    @Test(dataProvider = "doubleCompareOpProvider")
    static void EQDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            VectorMask<Double> mv = av.compare(VectorOperators.EQ, b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i]);
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpMaskProvider")
    static void EQDouble64VectorTestsBroadcastMaskedSmokeTest(IntFunction<double[]> fa,
                                IntFunction<double[]> fb, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            VectorMask<Double> mv = av.compare(VectorOperators.EQ, b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] == b[i]));
            }
        }
    }

    @Test(dataProvider = "doubleCompareOpProvider")
    static void EQDouble64VectorTestsBroadcastLongSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            VectorMask<Double> mv = av.compare(VectorOperators.EQ, (long)b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] == (double)((long)b[i]));
            }
        }
    }


    @Test(dataProvider = "doubleCompareOpMaskProvider")
    static void EQDouble64VectorTestsBroadcastLongMaskedSmokeTest(IntFunction<double[]> fa,
                                IntFunction<double[]> fb, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            VectorMask<Double> mv = av.compare(VectorOperators.EQ, (long)b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] == (double)((long)b[i])));
            }
        }
    }

    static double blend(double a, double b, boolean mask) {
        return mask ? b : a;
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void blendDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.blend(bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::blend);
    }

    @Test(dataProvider = "doubleUnaryOpShuffleProvider")
    static void RearrangeDouble64VectorTests(IntFunction<double[]> fa,
                                           BiFunction<Integer,Integer,int[]> fs) {
        double[] a = fa.apply(SPECIES.length());
        int[] order = fs.apply(a.length, SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.rearrange(VectorShuffle.fromArray(SPECIES, order, i)).intoArray(r, i);
            }
        }

        assertRearrangeArraysEquals(a, r, order, SPECIES.length());
    }

    @Test(dataProvider = "doubleUnaryOpShuffleMaskProvider")
    static void RearrangeDouble64VectorTestsMaskedSmokeTest(IntFunction<double[]> fa,
                                                          BiFunction<Integer,Integer,int[]> fs,
                                                          IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        int[] order = fs.apply(a.length, SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.rearrange(VectorShuffle.fromArray(SPECIES, order, i), vmask).intoArray(r, i);
        }

        assertRearrangeArraysEquals(a, r, order, mask, SPECIES.length());
    }
    @Test(dataProvider = "doubleUnaryOpProvider")
    static void getDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                int num_lanes = SPECIES.length();
                // Manually unroll because full unroll happens after intrinsification.
                // Unroll is needed because get intrinsic requires for index to be a known constant.
                if (num_lanes == 1) {
                    r[i]=av.lane(0);
                } else if (num_lanes == 2) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                } else if (num_lanes == 4) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                } else if (num_lanes == 8) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                } else if (num_lanes == 16) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                } else if (num_lanes == 32) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                    r[i+16]=av.lane(16);
                    r[i+17]=av.lane(17);
                    r[i+18]=av.lane(18);
                    r[i+19]=av.lane(19);
                    r[i+20]=av.lane(20);
                    r[i+21]=av.lane(21);
                    r[i+22]=av.lane(22);
                    r[i+23]=av.lane(23);
                    r[i+24]=av.lane(24);
                    r[i+25]=av.lane(25);
                    r[i+26]=av.lane(26);
                    r[i+27]=av.lane(27);
                    r[i+28]=av.lane(28);
                    r[i+29]=av.lane(29);
                    r[i+30]=av.lane(30);
                    r[i+31]=av.lane(31);
                } else if (num_lanes == 64) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                    r[i+16]=av.lane(16);
                    r[i+17]=av.lane(17);
                    r[i+18]=av.lane(18);
                    r[i+19]=av.lane(19);
                    r[i+20]=av.lane(20);
                    r[i+21]=av.lane(21);
                    r[i+22]=av.lane(22);
                    r[i+23]=av.lane(23);
                    r[i+24]=av.lane(24);
                    r[i+25]=av.lane(25);
                    r[i+26]=av.lane(26);
                    r[i+27]=av.lane(27);
                    r[i+28]=av.lane(28);
                    r[i+29]=av.lane(29);
                    r[i+30]=av.lane(30);
                    r[i+31]=av.lane(31);
                    r[i+32]=av.lane(32);
                    r[i+33]=av.lane(33);
                    r[i+34]=av.lane(34);
                    r[i+35]=av.lane(35);
                    r[i+36]=av.lane(36);
                    r[i+37]=av.lane(37);
                    r[i+38]=av.lane(38);
                    r[i+39]=av.lane(39);
                    r[i+40]=av.lane(40);
                    r[i+41]=av.lane(41);
                    r[i+42]=av.lane(42);
                    r[i+43]=av.lane(43);
                    r[i+44]=av.lane(44);
                    r[i+45]=av.lane(45);
                    r[i+46]=av.lane(46);
                    r[i+47]=av.lane(47);
                    r[i+48]=av.lane(48);
                    r[i+49]=av.lane(49);
                    r[i+50]=av.lane(50);
                    r[i+51]=av.lane(51);
                    r[i+52]=av.lane(52);
                    r[i+53]=av.lane(53);
                    r[i+54]=av.lane(54);
                    r[i+55]=av.lane(55);
                    r[i+56]=av.lane(56);
                    r[i+57]=av.lane(57);
                    r[i+58]=av.lane(58);
                    r[i+59]=av.lane(59);
                    r[i+60]=av.lane(60);
                    r[i+61]=av.lane(61);
                    r[i+62]=av.lane(62);
                    r[i+63]=av.lane(63);
                } else {
                    for (int j = 0; j < SPECIES.length(); j++) {
                        r[i+j]=av.lane(j);
                    }
                }
            }
        }

        assertArraysEquals(a, r, Double64VectorTests::get);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void BroadcastDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = new double[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector.broadcast(SPECIES, a[i]).intoArray(r, i);
            }
        }

        assertBroadcastArraysEquals(a, r);
    }





    @Test(dataProvider = "doubleUnaryOpProvider")
    static void ZeroDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = new double[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector.zero(SPECIES).intoArray(a, i);
            }
        }

        Assert.assertEquals(a, r);
    }




    static double[] sliceUnary(double[] a, int origin, int idx) {
        double[] res = new double[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = a[idx+i+origin];
            else
                res[i] = (double)0;
        }
        return res;
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void sliceUnaryDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = new double[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.slice(origin).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, origin, Double64VectorTests::sliceUnary);
    }
    static double[] sliceBinary(double[] a, double[] b, int origin, int idx) {
        double[] res = new double[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = a[idx+i+origin];
            else {
                res[i] = b[idx+j];
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void sliceBinaryDouble64VectorTestsBinary(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = new double[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.slice(origin, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, Double64VectorTests::sliceBinary);
    }
    static double[] slice(double[] a, double[] b, int origin, boolean[] mask, int idx) {
        double[] res = new double[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = mask[i] ? a[idx+i+origin] : (double)0;
            else {
                res[i] = mask[i] ? b[idx+j] : (double)0;
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void sliceDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
    IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        double[] r = new double[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.slice(origin, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, mask, Double64VectorTests::slice);
    }
    static double[] unsliceUnary(double[] a, int origin, int idx) {
        double[] res = new double[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i < origin)
                res[i] = (double)0;
            else {
                res[i] = a[idx+j];
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void unsliceUnaryDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = new double[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.unslice(origin).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, origin, Double64VectorTests::unsliceUnary);
    }
    static double[] unsliceBinary(double[] a, double[] b, int origin, int part, int idx) {
        double[] res = new double[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if (part == 0) {
                if (i < origin)
                    res[i] = b[idx+i];
                else {
                    res[i] = a[idx+j];
                    j++;
                }
            } else if (part == 1) {
                if (i < origin)
                    res[i] = a[idx+SPECIES.length()-origin+i];
                else {
                    res[i] = b[idx+origin+j];
                    j++;
                }
            }
        }
        return res;
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void unsliceBinaryDouble64VectorTestsBinary(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = new double[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        int part = (new java.util.Random()).nextInt(2);
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.unslice(origin, bv, part).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, part, Double64VectorTests::unsliceBinary);
    }
    static double[] unslice(double[] a, double[] b, int origin, int part, boolean[] mask, int idx) {
        double[] res = new double[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = b[idx+i+origin];
            else {
                res[i] = b[idx+j];
                j++;
            }
        }
        for (int i = 0; i < SPECIES.length(); i++){
            res[i] = mask[i] ? a[idx+i] : res[i];
        }
        double[] res1 = new double[SPECIES.length()];
        if (part == 0) {
            for (int i = 0, j = 0; i < SPECIES.length(); i++){
                if (i < origin)
                    res1[i] = b[idx+i];
                else {
                   res1[i] = res[j];
                   j++;
                }
            }
        } else if (part == 1) {
            for (int i = 0, j = 0; i < SPECIES.length(); i++){
                if (i < origin)
                    res1[i] = res[SPECIES.length()-origin+i];
                else {
                    res1[i] = b[idx+origin+j];
                    j++;
                }
            }
        }
        return res1;
    }

    @Test(dataProvider = "doubleBinaryOpMaskProvider")
    static void unsliceDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
    IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        double[] r = new double[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        int part = (new java.util.Random()).nextInt(2);
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.unslice(origin, bv, part, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, part, mask, Double64VectorTests::unslice);
    }

    static double SIN(double a) {
        return (double)(Math.sin((double)a));
    }

    static double strictSIN(double a) {
        return (double)(StrictMath.sin((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void SINDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.SIN).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::SIN, Double64VectorTests::strictSIN);
    }


    static double EXP(double a) {
        return (double)(Math.exp((double)a));
    }

    static double strictEXP(double a) {
        return (double)(StrictMath.exp((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void EXPDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.EXP).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::EXP, Double64VectorTests::strictEXP);
    }


    static double LOG1P(double a) {
        return (double)(Math.log1p((double)a));
    }

    static double strictLOG1P(double a) {
        return (double)(StrictMath.log1p((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void LOG1PDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LOG1P).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::LOG1P, Double64VectorTests::strictLOG1P);
    }


    static double LOG(double a) {
        return (double)(Math.log((double)a));
    }

    static double strictLOG(double a) {
        return (double)(StrictMath.log((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void LOGDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LOG).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::LOG, Double64VectorTests::strictLOG);
    }


    static double LOG10(double a) {
        return (double)(Math.log10((double)a));
    }

    static double strictLOG10(double a) {
        return (double)(StrictMath.log10((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void LOG10Double64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LOG10).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::LOG10, Double64VectorTests::strictLOG10);
    }


    static double EXPM1(double a) {
        return (double)(Math.expm1((double)a));
    }

    static double strictEXPM1(double a) {
        return (double)(StrictMath.expm1((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void EXPM1Double64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.EXPM1).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::EXPM1, Double64VectorTests::strictEXPM1);
    }


    static double COS(double a) {
        return (double)(Math.cos((double)a));
    }

    static double strictCOS(double a) {
        return (double)(StrictMath.cos((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void COSDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.COS).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::COS, Double64VectorTests::strictCOS);
    }


    static double TAN(double a) {
        return (double)(Math.tan((double)a));
    }

    static double strictTAN(double a) {
        return (double)(StrictMath.tan((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void TANDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.TAN).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::TAN, Double64VectorTests::strictTAN);
    }


    static double SINH(double a) {
        return (double)(Math.sinh((double)a));
    }

    static double strictSINH(double a) {
        return (double)(StrictMath.sinh((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void SINHDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.SINH).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::SINH, Double64VectorTests::strictSINH);
    }


    static double COSH(double a) {
        return (double)(Math.cosh((double)a));
    }

    static double strictCOSH(double a) {
        return (double)(StrictMath.cosh((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void COSHDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.COSH).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::COSH, Double64VectorTests::strictCOSH);
    }


    static double TANH(double a) {
        return (double)(Math.tanh((double)a));
    }

    static double strictTANH(double a) {
        return (double)(StrictMath.tanh((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void TANHDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.TANH).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::TANH, Double64VectorTests::strictTANH);
    }


    static double ASIN(double a) {
        return (double)(Math.asin((double)a));
    }

    static double strictASIN(double a) {
        return (double)(StrictMath.asin((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void ASINDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ASIN).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::ASIN, Double64VectorTests::strictASIN);
    }


    static double ACOS(double a) {
        return (double)(Math.acos((double)a));
    }

    static double strictACOS(double a) {
        return (double)(StrictMath.acos((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void ACOSDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ACOS).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::ACOS, Double64VectorTests::strictACOS);
    }


    static double ATAN(double a) {
        return (double)(Math.atan((double)a));
    }

    static double strictATAN(double a) {
        return (double)(StrictMath.atan((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void ATANDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ATAN).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::ATAN, Double64VectorTests::strictATAN);
    }


    static double CBRT(double a) {
        return (double)(Math.cbrt((double)a));
    }

    static double strictCBRT(double a) {
        return (double)(StrictMath.cbrt((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void CBRTDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.CBRT).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, r, Double64VectorTests::CBRT, Double64VectorTests::strictCBRT);
    }


    static double HYPOT(double a, double b) {
        return (double)(Math.hypot((double)a, (double)b));
    }

    static double strictHYPOT(double a, double b) {
        return (double)(StrictMath.hypot((double)a, (double)b));
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void HYPOTDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.HYPOT, bv).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, b, r, Double64VectorTests::HYPOT, Double64VectorTests::strictHYPOT);
    }



    static double POW(double a, double b) {
        return (double)(Math.pow((double)a, (double)b));
    }

    static double strictPOW(double a, double b) {
        return (double)(StrictMath.pow((double)a, (double)b));
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void POWDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.POW, bv).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, b, r, Double64VectorTests::POW, Double64VectorTests::strictPOW);
    }

    static double pow(double a, double b) {
        return (double)(Math.pow((double)a, (double)b));
    }

    static double strictpow(double a, double b) {
        return (double)(StrictMath.pow((double)a, (double)b));
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void powDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.pow(bv).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, b, r, Double64VectorTests::pow, Double64VectorTests::strictpow);
    }



    static double ATAN2(double a, double b) {
        return (double)(Math.atan2((double)a, (double)b));
    }

    static double strictATAN2(double a, double b) {
        return (double)(StrictMath.atan2((double)a, (double)b));
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void ATAN2Double64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ATAN2, bv).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(a, b, r, Double64VectorTests::ATAN2, Double64VectorTests::strictATAN2);
    }



    @Test(dataProvider = "doubleBinaryOpProvider")
    static void POWDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.POW, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEqualsWithinOneUlp(a, b, r, Double64VectorTests::POW, Double64VectorTests::strictPOW);
    }

    @Test(dataProvider = "doubleBinaryOpProvider")
    static void powDouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.pow(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEqualsWithinOneUlp(a, b, r, Double64VectorTests::pow, Double64VectorTests::strictpow);
    }



    static double FMA(double a, double b, double c) {
        return (double)(Math.fma(a, b, c));
    }
    static double fma(double a, double b, double c) {
        return (double)(Math.fma(a, b, c));
    }


    @Test(dataProvider = "doubleTernaryOpProvider")
    static void FMADouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb, IntFunction<double[]> fc) {
        int count = INVOC_COUNT;
        switch ("FMA") {
        case "fma": case "lanewise_FMA":
           // Math.fma uses BigDecimal
           count = Math.max(5, count/20); break;
        }
        final int INVOC_COUNT = count;
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] c = fc.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                DoubleVector cv = DoubleVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.FMA, bv, cv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, c, r, Double64VectorTests::FMA);
    }
    @Test(dataProvider = "doubleTernaryOpProvider")
    static void fmaDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb, IntFunction<double[]> fc) {
        int count = INVOC_COUNT;
        switch ("fma") {
        case "fma": case "lanewise_FMA":
           // Math.fma uses BigDecimal
           count = Math.max(5, count/20); break;
        }
        final int INVOC_COUNT = count;
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] c = fc.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            DoubleVector cv = DoubleVector.fromArray(SPECIES, c, i);
            av.fma(bv, cv).intoArray(r, i);
        }

        assertArraysEquals(a, b, c, r, Double64VectorTests::fma);
    }


    @Test(dataProvider = "doubleTernaryOpMaskProvider")
    static void FMADouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<double[]> fc, IntFunction<boolean[]> fm) {
        int count = INVOC_COUNT;
        switch ("FMA") {
        case "fma": case "lanewise_FMA":
           // Math.fma uses BigDecimal
           count = Math.max(5, count/20); break;
        }
        final int INVOC_COUNT = count;
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] c = fc.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
                DoubleVector cv = DoubleVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.FMA, bv, cv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, c, r, mask, Double64VectorTests::FMA);
    }





    @Test(dataProvider = "doubleTernaryOpProvider")
    static void FMADouble64VectorTestsBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb, IntFunction<double[]> fc) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] c = fc.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.lanewise(VectorOperators.FMA, bv, c[i]).intoArray(r, i);
        }
        assertBroadcastArraysEquals(a, b, c, r, Double64VectorTests::FMA);
    }

    @Test(dataProvider = "doubleTernaryOpProvider")
    static void FMADouble64VectorTestsAltBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb, IntFunction<double[]> fc) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] c = fc.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector cv = DoubleVector.fromArray(SPECIES, c, i);
            av.lanewise(VectorOperators.FMA, b[i], cv).intoArray(r, i);
        }
        assertAltBroadcastArraysEquals(a, b, c, r, Double64VectorTests::FMA);
    }


    @Test(dataProvider = "doubleTernaryOpMaskProvider")
    static void FMADouble64VectorTestsBroadcastMaskedSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<double[]> fc, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] c = fc.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, b, i);
            av.lanewise(VectorOperators.FMA, bv, c[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, c, r, mask, Double64VectorTests::FMA);
    }

    @Test(dataProvider = "doubleTernaryOpMaskProvider")
    static void FMADouble64VectorTestsAltBroadcastMaskedSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<double[]> fc, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] c = fc.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector cv = DoubleVector.fromArray(SPECIES, c, i);
            av.lanewise(VectorOperators.FMA, b[i], cv, vmask).intoArray(r, i);
        }

        assertAltBroadcastArraysEquals(a, b, c, r, mask, Double64VectorTests::FMA);
    }




    @Test(dataProvider = "doubleTernaryOpProvider")
    static void FMADouble64VectorTestsDoubleBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb, IntFunction<double[]> fc) {
        int count = INVOC_COUNT;
        switch ("FMA") {
        case "fma": case "lanewise_FMA":
           // Math.fma uses BigDecimal
           count = Math.max(5, count/20); break;
        }
        final int INVOC_COUNT = count;
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] c = fc.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.FMA, b[i], c[i]).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(a, b, c, r, Double64VectorTests::FMA);
    }
    @Test(dataProvider = "doubleTernaryOpProvider")
    static void fmaDouble64VectorTestsDoubleBroadcastSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb, IntFunction<double[]> fc) {
        int count = INVOC_COUNT;
        switch ("fma") {
        case "fma": case "lanewise_FMA":
           // Math.fma uses BigDecimal
           count = Math.max(5, count/20); break;
        }
        final int INVOC_COUNT = count;
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] c = fc.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.fma(b[i], c[i]).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(a, b, c, r, Double64VectorTests::fma);
    }


    @Test(dataProvider = "doubleTernaryOpMaskProvider")
    static void FMADouble64VectorTestsDoubleBroadcastMaskedSmokeTest(IntFunction<double[]> fa, IntFunction<double[]> fb,
                                          IntFunction<double[]> fc, IntFunction<boolean[]> fm) {
        int count = INVOC_COUNT;
        switch ("FMA") {
        case "fma": case "lanewise_FMA":
           // Math.fma uses BigDecimal
           count = Math.max(5, count/20); break;
        }
        final int INVOC_COUNT = count;
        double[] a = fa.apply(SPECIES.length());
        double[] b = fb.apply(SPECIES.length());
        double[] c = fc.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.FMA, b[i], c[i], vmask).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(a, b, c, r, mask, Double64VectorTests::FMA);
    }




    static double NEG(double a) {
        return (double)(-((double)a));
    }

    static double neg(double a) {
        return (double)(-((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void NEGDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NEG).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Double64VectorTests::NEG);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void negDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.neg().intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Double64VectorTests::neg);
    }

    @Test(dataProvider = "doubleUnaryOpMaskProvider")
    static void NEGMaskedDouble64VectorTests(IntFunction<double[]> fa,
                                                IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NEG, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, mask, Double64VectorTests::NEG);
    }

    static double ABS(double a) {
        return (double)(Math.abs((double)a));
    }

    static double abs(double a) {
        return (double)(Math.abs((double)a));
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void ABSDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ABS).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Double64VectorTests::ABS);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void absDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.abs().intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Double64VectorTests::abs);
    }

    @Test(dataProvider = "doubleUnaryOpMaskProvider")
    static void ABSMaskedDouble64VectorTests(IntFunction<double[]> fa,
                                                IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ABS, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, mask, Double64VectorTests::ABS);
    }








    static double SQRT(double a) {
        return (double)(Math.sqrt((double)a));
    }

    static double sqrt(double a) {
        return (double)(Math.sqrt((double)a));
    }



    @Test(dataProvider = "doubleUnaryOpProvider")
    static void SQRTDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.SQRT).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Double64VectorTests::SQRT);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void sqrtDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.sqrt().intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Double64VectorTests::sqrt);
    }



    @Test(dataProvider = "doubleUnaryOpMaskProvider")
    static void SQRTMaskedDouble64VectorTests(IntFunction<double[]> fa,
                                                IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.SQRT, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, mask, Double64VectorTests::SQRT);
    }

    static double[] gather(double a[], int ix, int[] b, int iy) {
        double[] res = new double[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++) {
            int bi = iy + i;
            res[i] = a[b[bi] + ix];
        }
        return res;
    }

    @Test(dataProvider = "doubleUnaryOpIndexProvider")
    static void gatherDouble64VectorTests(IntFunction<double[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        double[] a = fa.apply(SPECIES.length());
        int[] b    = fs.apply(a.length, SPECIES.length());
        double[] r = new double[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i, b, i);
                av.intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Double64VectorTests::gather);
    }
    static double[] gatherMasked(double a[], int ix, boolean[] mask, int[] b, int iy) {
        double[] res = new double[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++) {
            int bi = iy + i;
            if (mask[i]) {
              res[i] = a[b[bi] + ix];
            }
        }
        return res;
    }

    @Test(dataProvider = "doubleUnaryMaskedOpIndexProvider")
    static void gatherMaskedDouble64VectorTests(IntFunction<double[]> fa, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        int[] b    = fs.apply(a.length, SPECIES.length());
        double[] r = new double[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i, b, i, vmask);
                av.intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::gatherMasked);
    }

    static double[] scatter(double a[], int ix, int[] b, int iy) {
      double[] res = new double[SPECIES.length()];
      for (int i = 0; i < SPECIES.length(); i++) {
        int bi = iy + i;
        res[b[bi]] = a[i + ix];
      }
      return res;
    }

    @Test(dataProvider = "doubleUnaryOpIndexProvider")
    static void scatterDouble64VectorTests(IntFunction<double[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        double[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        double[] r = new double[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, b, i);
            }
        }

        assertArraysEquals(a, b, r, Double64VectorTests::scatter);
    }

    static double[] scatterMasked(double r[], double a[], int ix, boolean[] mask, int[] b, int iy) {
      // First, gather r.
      double[] oldVal = gather(r, ix, b, iy);
      double[] newVal = new double[SPECIES.length()];

      // Second, blending it with a.
      for (int i = 0; i < SPECIES.length(); i++) {
        newVal[i] = blend(oldVal[i], a[i+ix], mask[i]);
      }

      // Third, scatter: copy old value of r, and scatter it manually.
      double[] res = Arrays.copyOfRange(r, ix, ix+SPECIES.length());
      for (int i = 0; i < SPECIES.length(); i++) {
        int bi = iy + i;
        res[b[bi]] = newVal[i];
      }

      return res;
    }

    @Test(dataProvider = "scatterMaskedOpIndexProvider")
    static void scatterMaskedDouble64VectorTests(IntFunction<double[]> fa, IntFunction<double[]> fb, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        double[] r = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, b, i, vmask);
            }
        }

        assertArraysEquals(a, b, r, mask, Double64VectorTests::scatterMasked);
    }


    static long ADDLong(double[] a, int idx) {
        double res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res += a[i];
        }

        return (long)res;
    }

    static long ADDLong(double[] a) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res += ADDLong(a, i);
        }

        return res;
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void ADDReductionLongDouble64VectorTests(IntFunction<double[]> fa) {
        double[] a = fa.apply(SPECIES.length());
        long[] r = lfr.apply(SPECIES.length());
        long ra = 0;

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            r[i] = av.reduceLanesToLong(VectorOperators.ADD);
        }

        ra = 0;
        for (int i = 0; i < a.length; i ++) {
            ra += r[i];
        }

        assertReductionLongArraysEquals(a, r, ra, Double64VectorTests::ADDLong, Double64VectorTests::ADDLong);
    }

    static long ADDLongMasked(double[] a, int idx, boolean[] mask) {
        double res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res += a[i];
        }

        return (long)res;
    }

    static long ADDLongMasked(double[] a, boolean[] mask) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res += ADDLongMasked(a, i, mask);
        }

        return res;
    }

    @Test(dataProvider = "doubleUnaryOpMaskProvider")
    static void ADDReductionLongDouble64VectorTestsMasked(IntFunction<double[]> fa, IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        long[] r = lfr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        long ra = 0;

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            r[i] = av.reduceLanesToLong(VectorOperators.ADD, vmask);
        }

        ra = 0;
        for (int i = 0; i < a.length; i ++) {
            ra += r[i];
        }

        assertReductionLongArraysEqualsMasked(a, r, ra, mask, Double64VectorTests::ADDLongMasked, Double64VectorTests::ADDLongMasked);
    }

    @Test(dataProvider = "doubleUnaryOpSelectFromProvider")
    static void SelectFromDouble64VectorTests(IntFunction<double[]> fa,
                                           BiFunction<Integer,Integer,double[]> fs) {
        double[] a = fa.apply(SPECIES.length());
        double[] order = fs.apply(a.length, SPECIES.length());
        double[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, order, i);
            bv.selectFrom(av).intoArray(r, i);
        }

        assertSelectFromArraysEquals(a, r, order, SPECIES.length());
    }

    @Test(dataProvider = "doubleUnaryOpSelectFromMaskProvider")
    static void SelectFromDouble64VectorTestsMaskedSmokeTest(IntFunction<double[]> fa,
                                                           BiFunction<Integer,Integer,double[]> fs,
                                                           IntFunction<boolean[]> fm) {
        double[] a = fa.apply(SPECIES.length());
        double[] order = fs.apply(a.length, SPECIES.length());
        double[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Double> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            DoubleVector av = DoubleVector.fromArray(SPECIES, a, i);
            DoubleVector bv = DoubleVector.fromArray(SPECIES, order, i);
            bv.selectFrom(av, vmask).intoArray(r, i);
        }

        assertSelectFromArraysEquals(a, r, order, mask, SPECIES.length());
    }

    @Test
    static void ElementSizeDouble64VectorTests() {
        DoubleVector av = DoubleVector.zero(SPECIES);
        int elsize = av.elementSize();
        Assert.assertEquals(elsize, Double.SIZE);
    }

    @Test
    static void VectorShapeDouble64VectorTests() {
        DoubleVector av = DoubleVector.zero(SPECIES);
        VectorShape vsh = av.shape();
        assert(vsh.equals(VectorShape.S_64_BIT));
    }
}

