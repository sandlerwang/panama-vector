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
 * @run testng/othervm -ea -esa -Xbatch Long256VectorTests
 */

// -- This file was mechanically generated: Do not edit! -- //

import jdk.incubator.vector.VectorShape;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorShuffle;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.Vector;

import jdk.incubator.vector.LongVector;

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
public class Long256VectorTests extends AbstractVectorTest {

    static final VectorSpecies<Long> SPECIES =
                LongVector.SPECIES_256;

    static final int INVOC_COUNT = Integer.getInteger("jdk.incubator.vector.test.loop-iterations", 100);


    static final int BUFFER_REPS = Integer.getInteger("jdk.incubator.vector.test.buffer-vectors", 25000 / 256);

    static final int BUFFER_SIZE = Integer.getInteger("jdk.incubator.vector.test.buffer-size", BUFFER_REPS * (256 / 8));

    interface FUnOp {
        long apply(long a);
    }

    static void assertArraysEquals(long[] a, long[] r, FUnOp f) {
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
        long[] apply(long a);
    }

    static void assertArraysEquals(long[] a, long[] r, FUnArrayOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a[i]));
            }
        } catch (AssertionError e) {
            long[] ref = f.apply(a[i]);
            long[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i);
        }
    }

    static void assertArraysEquals(long[] a, long[] r, boolean[] mask, FUnOp f) {
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
        long apply(long[] a, int idx);
    }

    interface FReductionAllOp {
        long apply(long[] a);
    }

    static void assertReductionArraysEquals(long[] a, long[] b, long c,
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
        long apply(long[] a, int idx, boolean[] mask);
    }

    interface FReductionAllMaskedOp {
        long apply(long[] a, boolean[] mask);
    }

    static void assertReductionArraysEqualsMasked(long[] a, long[] b, long c, boolean[] mask,
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

    static void assertInsertArraysEquals(long[] a, long[] b, long element, int index) {
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

    static void assertRearrangeArraysEquals(long[] a, long[] r, int[] order, int vector_len) {
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

    static void assertBroadcastArraysEquals(long[]a, long[]r) {
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
        long apply(long a, long b);
    }

    interface FBinMaskOp {
        long apply(long a, long b, boolean m);

        static FBinMaskOp lift(FBinOp f) {
            return (a, b, m) -> m ? f.apply(a, b) : a;
        }
    }

    static void assertArraysEquals(long[] a, long[] b, long[] r, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i]), "(" + a[i] + ", " + b[i] + ") at index #" + i);
        }
    }

    static void assertBroadcastArraysEquals(long[] a, long[] b, long[] r, FBinOp f) {
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

    static void assertArraysEquals(long[] a, long[] b, long[] r, boolean[] mask, FBinOp f) {
        assertArraysEquals(a, b, r, mask, FBinMaskOp.lift(f));
    }

    static void assertArraysEquals(long[] a, long[] b, long[] r, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(long[] a, long[] b, long[] r, boolean[] mask, FBinOp f) {
        assertBroadcastArraysEquals(a, b, r, mask, FBinMaskOp.lift(f));
    }

    static void assertBroadcastArraysEquals(long[] a, long[] b, long[] r, boolean[] mask, FBinMaskOp f) {
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

    static void assertShiftArraysEquals(long[] a, long[] b, long[] r, FBinOp f) {
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

    static void assertShiftArraysEquals(long[] a, long[] b, long[] r, boolean[] mask, FBinOp f) {
        assertShiftArraysEquals(a, b, r, mask, FBinMaskOp.lift(f));
    }

    static void assertShiftArraysEquals(long[] a, long[] b, long[] r, boolean[] mask, FBinMaskOp f) {
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
        long apply(long a, long b, long c);
    }

    interface FTernMaskOp {
        long apply(long a, long b, long c, boolean m);

        static FTernMaskOp lift(FTernOp f) {
            return (a, b, c, m) -> m ? f.apply(a, b, c) : a;
        }
    }

    static void assertArraysEquals(long[] a, long[] b, long[] c, long[] r, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " + c[i]);
        }
    }

    static void assertArraysEquals(long[] a, long[] b, long[] c, long[] r, boolean[] mask, FTernOp f) {
        assertArraysEquals(a, b, c, r, mask, FTernMaskOp.lift(f));
    }

    static void assertArraysEquals(long[] a, long[] b, long[] c, long[] r, boolean[] mask, FTernMaskOp f) {
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

    static void assertBroadcastArraysEquals(long[] a, long[] b, long[] c, long[] r, FTernOp f) {
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

    static void assertBroadcastArraysEquals(long[] a, long[] b, long[] c, long[] r, boolean[] mask,
                                            FTernOp f) {
        assertBroadcastArraysEquals(a, b, c, r, mask, FTernMaskOp.lift(f));
    }

    static void assertBroadcastArraysEquals(long[] a, long[] b, long[] c, long[] r, boolean[] mask,
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

    static void assertDoubleBroadcastArraysEquals(long[] a, long[] b, long[] c, long[] r, FTernOp f) {
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

    static void assertDoubleBroadcastArraysEquals(long[] a, long[] b, long[] c, long[] r, boolean[] mask,
                                                  FTernOp f) {
        assertDoubleBroadcastArraysEquals(a, b, c, r, mask, FTernMaskOp.lift(f));
    }

    static void assertDoubleBroadcastArraysEquals(long[] a, long[] b, long[] c, long[] r, boolean[] mask, 
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



    interface FBinArrayOp {
        long apply(long[] a, int b);
    }

    static void assertArraysEquals(long[] a, long[] r, FBinArrayOp f) {
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
        long[] apply(long[] a, int ix, int[] b, int iy);
    }

    static void assertArraysEquals(long[] a, int[] b, long[] r, FGatherScatterOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, i, b, i));
            }
        } catch (AssertionError e) {
            long[] ref = f.apply(a, i, b, i);
            long[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + " at index #" + i);
        }
    }

    interface FGatherMaskedOp {
        long[] apply(long[] a, int ix, boolean[] mask, int[] b, int iy);
    }

    interface FScatterMaskedOp {
        long[] apply(long[] r, long[] a, int ix, boolean[] mask, int[] b, int iy);
    }

    static void assertArraysEquals(long[] a, int[] b, long[] r, boolean[] mask, FGatherMaskedOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, i, mask, b, i));
            }
        } catch (AssertionError e) {
            long[] ref = f.apply(a, i, mask, b, i);
            long[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
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

    static void assertArraysEquals(long[] a, int[] b, long[] r, boolean[] mask, FScatterMaskedOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(r, a, i, mask, b, i));
            }
        } catch (AssertionError e) {
            long[] ref = f.apply(r, a, i, mask, b, i);
            long[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
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
        long[] apply(long[] a, int origin, int idx);
    }

    static void assertArraysEquals(long[] a, long[] r, int origin, FLaneOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, origin, i));
            }
        } catch (AssertionError e) {
            long[] ref = f.apply(a, origin, i);
            long[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i);
        }
    }

    interface FLaneBop {
        long[] apply(long[] a, long[] b, int origin, int idx);
    }

    static void assertArraysEquals(long[] a, long[] b, long[] r, int origin, FLaneBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, i));
            }
        } catch (AssertionError e) {
            long[] ref = f.apply(a, b, origin, i);
            long[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin);
        }
    }

    interface FLaneMaskedBop {
        long[] apply(long[] a, long[] b, int origin, boolean[] mask, int idx);
    }

    static void assertArraysEquals(long[] a, long[] b, long[] r, int origin, boolean[] mask, FLaneMaskedBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, mask, i));
            }
        } catch (AssertionError e) {
            long[] ref = f.apply(a, b, origin, mask, i);
            long[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin);
        }
    }

    interface FLanePartBop {
        long[] apply(long[] a, long[] b, int origin, int part, int idx);
    }

    static void assertArraysEquals(long[] a, long[] b, long[] r, int origin, int part, FLanePartBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, part, i));
            }
        } catch (AssertionError e) {
            long[] ref = f.apply(a, b, origin, part, i);
            long[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin
              + ", with part #" + part);
        }
    }

    interface FLanePartMaskedBop {
        long[] apply(long[] a, long[] b, int origin, int part, boolean[] mask, int idx);
    }

    static void assertArraysEquals(long[] a, long[] b, long[] r, int origin, int part, boolean[] mask, FLanePartMaskedBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, part, mask, i));
            }
        } catch (AssertionError e) {
            long[] ref = f.apply(a, b, origin, part, mask, i);
            long[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin
              + ", with part #" + part);
        }
    }

    static long bits(long e) {
        return  e;
    }

    static final List<IntFunction<long[]>> LONG_GENERATORS = List.of(
            withToString("long[-i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (long)(-i * 5));
            }),
            withToString("long[i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (long)(i * 5));
            }),
            withToString("long[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (((long)(i + 1) == 0) ? 1 : (long)(i + 1)));
            }),
            withToString("long[cornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> cornerCaseValue(i));
            })
    );

    // Create combinations of pairs
    // @@@ Might be sensitive to order e.g. div by 0
    static final List<List<IntFunction<long[]>>> LONG_GENERATOR_PAIRS =
        Stream.of(LONG_GENERATORS.get(0)).
                flatMap(fa -> LONG_GENERATORS.stream().skip(1).map(fb -> List.of(fa, fb))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] boolUnaryOpProvider() {
        return BOOL_ARRAY_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    static final List<List<IntFunction<long[]>>> LONG_GENERATOR_TRIPLES =
        LONG_GENERATOR_PAIRS.stream().
                flatMap(pair -> LONG_GENERATORS.stream().map(f -> List.of(pair.get(0), pair.get(1), f))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] longBinaryOpProvider() {
        return LONG_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] longIndexedOpProvider() {
        return LONG_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] longBinaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> LONG_GENERATOR_PAIRS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] longTernaryOpProvider() {
        return LONG_GENERATOR_TRIPLES.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] longTernaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> LONG_GENERATOR_TRIPLES.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] longUnaryOpProvider() {
        return LONG_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] longUnaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> LONG_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] longUnaryOpShuffleProvider() {
        return INT_SHUFFLE_GENERATORS.stream().
                flatMap(fs -> LONG_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] longUnaryOpIndexProvider() {
        return INT_INDEX_GENERATORS.stream().
                flatMap(fs -> LONG_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] longUnaryMaskedOpIndexProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
          flatMap(fs -> INT_INDEX_GENERATORS.stream().flatMap(fm ->
            LONG_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm, fs};
            }))).
            toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] scatterMaskedOpIndexProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
          flatMap(fs -> INT_INDEX_GENERATORS.stream().flatMap(fm ->
            LONG_GENERATORS.stream().flatMap(fn ->
              LONG_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fn, fm, fs};
            })))).
            toArray(Object[][]::new);
    }

    static final List<IntFunction<long[]>> LONG_COMPARE_GENERATORS = List.of(
            withToString("long[i]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (long)i);
            }),
            withToString("long[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (long)(i + 1));
            }),
            withToString("long[i - 2]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (long)(i - 2));
            }),
            withToString("long[zigZag(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> i%3 == 0 ? (long)i : (i%3 == 1 ? (long)(i + 1) : (long)(i - 2)));
            }),
            withToString("long[cornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> cornerCaseValue(i));
            })
    );

    static final List<List<IntFunction<long[]>>> LONG_TEST_GENERATOR_ARGS =
        LONG_COMPARE_GENERATORS.stream().
                map(fa -> List.of(fa)).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] longTestOpProvider() {
        return LONG_TEST_GENERATOR_ARGS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    static final List<List<IntFunction<long[]>>> LONG_COMPARE_GENERATOR_PAIRS =
        LONG_COMPARE_GENERATORS.stream().
                flatMap(fa -> LONG_COMPARE_GENERATORS.stream().map(fb -> List.of(fa, fb))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] longCompareOpProvider() {
        return LONG_COMPARE_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    interface ToLongF {
        long apply(int i);
    }

    static long[] fill(int s , ToLongF f) {
        return fill(new long[s], f);
    }

    static long[] fill(long[] a, ToLongF f) {
        for (int i = 0; i < a.length; i++) {
            a[i] = f.apply(i);
        }
        return a;
    }

    static long cornerCaseValue(int i) {
        switch(i % 5) {
            case 0:
                return Long.MAX_VALUE;
            case 1:
                return Long.MIN_VALUE;
            case 2:
                return Long.MIN_VALUE;
            case 3:
                return Long.MAX_VALUE;
            default:
                return (long)0;
        }
    }
   static long get(long[] a, int i) {
       return (long) a[i];
   }

   static final IntFunction<long[]> fr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new long[length];
    };

    static final IntFunction<boolean[]> fmr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new boolean[length];
    };

    static void replaceZero(long[] a, long v) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0) {
                a[i] = v;
            }
        }
    }

    static void replaceZero(long[] a, boolean[] mask, long v) {
        for (int i = 0; i < a.length; i++) {
            if (mask[i % mask.length] && a[i] == 0) {
                a[i] = v;
            }
        }
    }

    @Test
    static void smokeTest1() {
        LongVector three = LongVector.broadcast(SPECIES, (byte)-3);
        LongVector three2 = (LongVector) SPECIES.broadcast(-3);
        assert(three.eq(three2).allTrue());
        LongVector three3 = three2.broadcast(1).broadcast(-3);
        assert(three.eq(three3).allTrue());
        int scale = 2;
        Class<?> ETYPE = long.class;
        if (ETYPE == double.class || ETYPE == long.class)
            scale = 1000000;
        else if (ETYPE == byte.class && SPECIES.length() >= 64)
            scale = 1;
        LongVector higher = three.addIndex(scale);
        VectorMask<Long> m = three.compare(VectorOperators.LE, higher);
        assert(m.allTrue());
        m = higher.min((long)-1).test(VectorOperators.IS_NEGATIVE);
        assert(m.allTrue());
        long max = higher.reduceLanes(VectorOperators.MAX);
        assert(max == -3 + scale * (SPECIES.length()-1));
    }

    private static long[]
    bothToArray(LongVector a, LongVector b) {
        long[] r = new long[a.length() + b.length()];
        a.intoArray(r, 0);
        b.intoArray(r, a.length());
        return r;
    }

    @Test
    static void smokeTest2() {
        // Do some zipping and shuffling.
        LongVector io = (LongVector) SPECIES.broadcast(0).addIndex(1);
        LongVector io2 = (LongVector) VectorShuffle.iota(SPECIES,0,1,false).toVector();
        Assert.assertEquals(io, io2);
        LongVector a = io.add((long)1); //[1,2]
        LongVector b = a.neg();  //[-1,-2]
        long[] abValues = bothToArray(a,b); //[1,2,-1,-2]
        VectorShuffle<Long> zip0 = VectorShuffle.makeZip(SPECIES, 0);
        VectorShuffle<Long> zip1 = VectorShuffle.makeZip(SPECIES, 1);
        LongVector zab0 = a.rearrange(zip0,b); //[1,-1]
        LongVector zab1 = a.rearrange(zip1,b); //[2,-2]
        long[] zabValues = bothToArray(zab0, zab1); //[1,-1,2,-2]
        // manually zip
        long[] manual = new long[zabValues.length];
        for (int i = 0; i < manual.length; i += 2) {
            manual[i+0] = abValues[i/2];
            manual[i+1] = abValues[a.length() + i/2];
        }
        Assert.assertEquals(Arrays.toString(zabValues), Arrays.toString(manual));
        VectorShuffle<Long> unz0 = VectorShuffle.makeUnzip(SPECIES, 0);
        VectorShuffle<Long> unz1 = VectorShuffle.makeUnzip(SPECIES, 1);
        LongVector uab0 = zab0.rearrange(unz0,zab1);
        LongVector uab1 = zab0.rearrange(unz1,zab1);
        long[] abValues1 = bothToArray(uab0, uab1);
        Assert.assertEquals(Arrays.toString(abValues), Arrays.toString(abValues1));
    }

    static void iotaShuffle() {
        LongVector io = (LongVector) SPECIES.broadcast(0).addIndex(1);
        LongVector io2 = (LongVector) VectorShuffle.iota(SPECIES, 0 , 1, false).toVector();
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
        Assert.assertEquals(asIntegral.species(), SPECIES);
    }

    @Test
    void viewAsFloatingLanesTest() {
        Vector<?> asFloating = SPECIES.zero().viewAsFloatingLanes();
        VectorSpecies<?> asFloatingSpecies = asFloating.species();
        Assert.assertNotEquals(asFloatingSpecies.elementType(), SPECIES.elementType());
        Assert.assertEquals(asFloatingSpecies.vectorShape(), SPECIES.vectorShape());
        Assert.assertEquals(asFloatingSpecies.length(), SPECIES.length());
        Assert.assertEquals(asFloating.viewAsIntegralLanes().species(), SPECIES);
    }

    @Test
    // Test div by 0.
    static void bitwiseDivByZeroSmokeTest() {
        try {
            LongVector a = (LongVector) SPECIES.broadcast(0).addIndex(1);
            LongVector b = (LongVector) SPECIES.broadcast(0);
            a.div(b);
            Assert.fail();
        } catch (ArithmeticException e) {
        }

        try {
            LongVector a = (LongVector) SPECIES.broadcast(0).addIndex(1);
            LongVector b = (LongVector) SPECIES.broadcast(0);
            VectorMask<Long> m = a.lt((long) 1);
            a.div(b, m);
            Assert.fail();
        } catch (ArithmeticException e) {
        }
    }
    static long ADD(long a, long b) {
        return (long)(a + b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void ADDLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ADD, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::ADD);
    }
    static long add(long a, long b) {
        return (long)(a + b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void addLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.add(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Long256VectorTests::add);
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void ADDLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ADD, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::ADD);
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void addLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.add(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::add);
    }
    static long SUB(long a, long b) {
        return (long)(a - b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void SUBLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUB, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::SUB);
    }
    static long sub(long a, long b) {
        return (long)(a - b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void subLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.sub(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Long256VectorTests::sub);
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void SUBLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUB, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::SUB);
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void subLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.sub(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::sub);
    }
    static long MUL(long a, long b) {
        return (long)(a * b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void MULLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MUL, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::MUL);
    }
    static long mul(long a, long b) {
        return (long)(a * b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void mulLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.mul(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Long256VectorTests::mul);
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void MULLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MUL, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::MUL);
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void mulLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.mul(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::mul);
    }



    static long DIV(long a, long b) {
        return (long)(a / b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void DIVLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        replaceZero(b, (long) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.DIV, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::DIV);
    }
    static long div(long a, long b) {
        return (long)(a / b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void divLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        replaceZero(b, (long) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.div(bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::div);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void DIVLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        replaceZero(b, mask, (long) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.DIV, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::DIV);
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void divLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        replaceZero(b, mask, (long) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.div(bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::div);
    }

    static long FIRST_NONZERO(long a, long b) {
        return (long)((a)!=0?a:b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void FIRST_NONZEROLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.FIRST_NONZERO, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::FIRST_NONZERO);
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void FIRST_NONZEROLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.FIRST_NONZERO, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::FIRST_NONZERO);
    }

    static long AND(long a, long b) {
        return (long)(a & b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void ANDLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::AND);
    }
    static long and(long a, long b) {
        return (long)(a & b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void andLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.and(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Long256VectorTests::and);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void ANDLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::AND);
    }


    static long AND_NOT(long a, long b) {
        return (long)(a & ~b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void AND_NOTLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND_NOT, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::AND_NOT);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void AND_NOTLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND_NOT, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::AND_NOT);
    }


    static long OR(long a, long b) {
        return (long)(a | b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void ORLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.OR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::OR);
    }
    static long or(long a, long b) {
        return (long)(a | b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void orLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.or(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Long256VectorTests::or);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void ORLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.OR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::OR);
    }


    static long XOR(long a, long b) {
        return (long)(a ^ b);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void XORLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.XOR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::XOR);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void XORLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.XOR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::XOR);
    }


    @Test(dataProvider = "longBinaryOpProvider")
    static void addLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.add(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Long256VectorTests::add);
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void addLong256VectorTestsBroadcastMaskedSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.add(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Long256VectorTests::add);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void subLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.sub(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Long256VectorTests::sub);
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void subLong256VectorTestsBroadcastMaskedSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.sub(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Long256VectorTests::sub);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void mulLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.mul(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Long256VectorTests::mul);
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void mulLong256VectorTestsBroadcastMaskedSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.mul(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Long256VectorTests::mul);
    }




    @Test(dataProvider = "longBinaryOpProvider")
    static void divLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        replaceZero(b, (long) 1);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.div(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Long256VectorTests::div);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void divLong256VectorTestsBroadcastMaskedSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        replaceZero(b, (long) 1);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.div(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Long256VectorTests::div);
    }



    @Test(dataProvider = "longBinaryOpProvider")
    static void ORLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.OR, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Long256VectorTests::OR);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void orLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.or(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Long256VectorTests::or);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void ORLong256VectorTestsBroadcastMaskedSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.OR, b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Long256VectorTests::OR);
    }


    static long LSHL(long a, long b) {
        return (long)((a << b));
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void LSHLLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHL, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::LSHL);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void LSHLLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHL, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::LSHL);
    }






    static long ASHR(long a, long b) {
        return (long)((a >> b));
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void ASHRLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ASHR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::ASHR);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void ASHRLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ASHR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::ASHR);
    }






    static long LSHR(long a, long b) {
        return (long)((a >>> b));
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void LSHRLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::LSHR);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void LSHRLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::LSHR);
    }






    static long LSHL_unary(long a, long b) {
        return (long)((a << b));
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void LSHLLong256VectorTestsShift(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHL, (int)b[i]).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, Long256VectorTests::LSHL_unary);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void LSHLLong256VectorTestsShift(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHL, (int)b[i], vmask).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, mask, Long256VectorTests::LSHL_unary);
    }






    static long LSHR_unary(long a, long b) {
        return (long)((a >>> b));
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void LSHRLong256VectorTestsShift(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHR, (int)b[i]).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, Long256VectorTests::LSHR_unary);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void LSHRLong256VectorTestsShift(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHR, (int)b[i], vmask).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, mask, Long256VectorTests::LSHR_unary);
    }






    static long ASHR_unary(long a, long b) {
        return (long)((a >> b));
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void ASHRLong256VectorTestsShift(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ASHR, (int)b[i]).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, Long256VectorTests::ASHR_unary);
    }



    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void ASHRLong256VectorTestsShift(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ASHR, (int)b[i], vmask).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, mask, Long256VectorTests::ASHR_unary);
    }





    static long MIN(long a, long b) {
        return (long)(Math.min(a, b));
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void MINLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MIN, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::MIN);
    }
    static long min(long a, long b) {
        return (long)(Math.min(a, b));
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void minLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.min(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Long256VectorTests::min);
    }
    static long MAX(long a, long b) {
        return (long)(Math.max(a, b));
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void MAXLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MAX, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::MAX);
    }
    static long max(long a, long b) {
        return (long)(Math.max(a, b));
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void maxLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.max(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Long256VectorTests::max);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void MINLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.MIN, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Long256VectorTests::MIN);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void minLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.min(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Long256VectorTests::min);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void MAXLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.MAX, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Long256VectorTests::MAX);
    }

    @Test(dataProvider = "longBinaryOpProvider")
    static void maxLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.max(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Long256VectorTests::max);
    }

    static long AND(long[] a, int idx) {
        long res = -1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res &= a[i];
        }

        return res;
    }

    static long AND(long[] a) {
        long res = -1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            long tmp = -1;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp &= a[i + j];
            }
            res &= tmp;
        }

        return res;
    }


    @Test(dataProvider = "longUnaryOpProvider")
    static void ANDLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        long ra = -1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.AND);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = -1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra &= av.reduceLanes(VectorOperators.AND);
            }
        }

        assertReductionArraysEquals(a, r, ra, Long256VectorTests::AND, Long256VectorTests::AND);
    }


    static long ANDMasked(long[] a, int idx, boolean[] mask) {
        long res = -1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res &= a[i];
        }

        return res;
    }

    static long ANDMasked(long[] a, boolean[] mask) {
        long res = -1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            long tmp = -1;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp &= a[i + j];
            }
            res &= tmp;
        }

        return res;
    }


    @Test(dataProvider = "longUnaryOpMaskProvider")
    static void ANDLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        long ra = -1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.AND, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = -1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra &= av.reduceLanes(VectorOperators.AND, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Long256VectorTests::ANDMasked, Long256VectorTests::ANDMasked);
    }


    static long OR(long[] a, int idx) {
        long res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res |= a[i];
        }

        return res;
    }

    static long OR(long[] a) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            long tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp |= a[i + j];
            }
            res |= tmp;
        }

        return res;
    }


    @Test(dataProvider = "longUnaryOpProvider")
    static void ORLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        long ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.OR);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra |= av.reduceLanes(VectorOperators.OR);
            }
        }

        assertReductionArraysEquals(a, r, ra, Long256VectorTests::OR, Long256VectorTests::OR);
    }


    static long ORMasked(long[] a, int idx, boolean[] mask) {
        long res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res |= a[i];
        }

        return res;
    }

    static long ORMasked(long[] a, boolean[] mask) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            long tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp |= a[i + j];
            }
            res |= tmp;
        }

        return res;
    }


    @Test(dataProvider = "longUnaryOpMaskProvider")
    static void ORLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        long ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.OR, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra |= av.reduceLanes(VectorOperators.OR, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Long256VectorTests::ORMasked, Long256VectorTests::ORMasked);
    }


    static long XOR(long[] a, int idx) {
        long res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res ^= a[i];
        }

        return res;
    }

    static long XOR(long[] a) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            long tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp ^= a[i + j];
            }
            res ^= tmp;
        }

        return res;
    }


    @Test(dataProvider = "longUnaryOpProvider")
    static void XORLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        long ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.XOR);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra ^= av.reduceLanes(VectorOperators.XOR);
            }
        }

        assertReductionArraysEquals(a, r, ra, Long256VectorTests::XOR, Long256VectorTests::XOR);
    }


    static long XORMasked(long[] a, int idx, boolean[] mask) {
        long res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res ^= a[i];
        }

        return res;
    }

    static long XORMasked(long[] a, boolean[] mask) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            long tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp ^= a[i + j];
            }
            res ^= tmp;
        }

        return res;
    }


    @Test(dataProvider = "longUnaryOpMaskProvider")
    static void XORLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        long ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.XOR, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra ^= av.reduceLanes(VectorOperators.XOR, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Long256VectorTests::XORMasked, Long256VectorTests::XORMasked);
    }

    static long ADD(long[] a, int idx) {
        long res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res += a[i];
        }

        return res;
    }

    static long ADD(long[] a) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            long tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp += a[i + j];
            }
            res += tmp;
        }

        return res;
    }
    @Test(dataProvider = "longUnaryOpProvider")
    static void ADDLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        long ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.ADD);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra += av.reduceLanes(VectorOperators.ADD);
            }
        }

        assertReductionArraysEquals(a, r, ra, Long256VectorTests::ADD, Long256VectorTests::ADD);
    }
    static long ADDMasked(long[] a, int idx, boolean[] mask) {
        long res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res += a[i];
        }

        return res;
    }

    static long ADDMasked(long[] a, boolean[] mask) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            long tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp += a[i + j];
            }
            res += tmp;
        }

        return res;
    }
    @Test(dataProvider = "longUnaryOpMaskProvider")
    static void ADDLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        long ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.ADD, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra += av.reduceLanes(VectorOperators.ADD, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Long256VectorTests::ADDMasked, Long256VectorTests::ADDMasked);
    }
    static long MUL(long[] a, int idx) {
        long res = 1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res *= a[i];
        }

        return res;
    }

    static long MUL(long[] a) {
        long res = 1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            long tmp = 1;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp *= a[i + j];
            }
            res *= tmp;
        }

        return res;
    }
    @Test(dataProvider = "longUnaryOpProvider")
    static void MULLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        long ra = 1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MUL);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra *= av.reduceLanes(VectorOperators.MUL);
            }
        }

        assertReductionArraysEquals(a, r, ra, Long256VectorTests::MUL, Long256VectorTests::MUL);
    }
    static long MULMasked(long[] a, int idx, boolean[] mask) {
        long res = 1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res *= a[i];
        }

        return res;
    }

    static long MULMasked(long[] a, boolean[] mask) {
        long res = 1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            long tmp = 1;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp *= a[i + j];
            }
            res *= tmp;
        }

        return res;
    }
    @Test(dataProvider = "longUnaryOpMaskProvider")
    static void MULLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        long ra = 1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MUL, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra *= av.reduceLanes(VectorOperators.MUL, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Long256VectorTests::MULMasked, Long256VectorTests::MULMasked);
    }
    static long MIN(long[] a, int idx) {
        long res = Long.MAX_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (long)Math.min(res, a[i]);
        }

        return res;
    }

    static long MIN(long[] a) {
        long res = Long.MAX_VALUE;
        for (int i = 0; i < a.length; i++) {
            res = (long)Math.min(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "longUnaryOpProvider")
    static void MINLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        long ra = Long.MAX_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MIN);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Long.MAX_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra = (long)Math.min(ra, av.reduceLanes(VectorOperators.MIN));
            }
        }

        assertReductionArraysEquals(a, r, ra, Long256VectorTests::MIN, Long256VectorTests::MIN);
    }
    static long MINMasked(long[] a, int idx, boolean[] mask) {
        long res = Long.MAX_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res = (long)Math.min(res, a[i]);
        }

        return res;
    }

    static long MINMasked(long[] a, boolean[] mask) {
        long res = Long.MAX_VALUE;
        for (int i = 0; i < a.length; i++) {
            if(mask[i % SPECIES.length()])
                res = (long)Math.min(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "longUnaryOpMaskProvider")
    static void MINLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        long ra = Long.MAX_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MIN, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Long.MAX_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra = (long)Math.min(ra, av.reduceLanes(VectorOperators.MIN, vmask));
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Long256VectorTests::MINMasked, Long256VectorTests::MINMasked);
    }
    static long MAX(long[] a, int idx) {
        long res = Long.MIN_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (long)Math.max(res, a[i]);
        }

        return res;
    }

    static long MAX(long[] a) {
        long res = Long.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            res = (long)Math.max(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "longUnaryOpProvider")
    static void MAXLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        long ra = Long.MIN_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MAX);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Long.MIN_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra = (long)Math.max(ra, av.reduceLanes(VectorOperators.MAX));
            }
        }

        assertReductionArraysEquals(a, r, ra, Long256VectorTests::MAX, Long256VectorTests::MAX);
    }
    static long MAXMasked(long[] a, int idx, boolean[] mask) {
        long res = Long.MIN_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res = (long)Math.max(res, a[i]);
        }

        return res;
    }

    static long MAXMasked(long[] a, boolean[] mask) {
        long res = Long.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            if(mask[i % SPECIES.length()])
                res = (long)Math.max(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "longUnaryOpMaskProvider")
    static void MAXLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        long ra = Long.MIN_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MAX, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Long.MIN_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                ra = (long)Math.max(ra, av.reduceLanes(VectorOperators.MAX, vmask));
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Long256VectorTests::MAXMasked, Long256VectorTests::MAXMasked);
    }

    static boolean anyTrue(boolean[] a, int idx) {
        boolean res = false;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res |= a[i];
        }

        return res;
    }


    @Test(dataProvider = "boolUnaryOpProvider")
    static void anyTrueLong256VectorTests(IntFunction<boolean[]> fm) {
        boolean[] mask = fm.apply(SPECIES.length());
        boolean[] r = fmr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < mask.length; i += SPECIES.length()) {
                VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, i);
                r[i] = vmask.anyTrue();
            }
        }

        assertReductionBoolArraysEquals(mask, r, Long256VectorTests::anyTrue);
    }


    static boolean allTrue(boolean[] a, int idx) {
        boolean res = true;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res &= a[i];
        }

        return res;
    }


    @Test(dataProvider = "boolUnaryOpProvider")
    static void allTrueLong256VectorTests(IntFunction<boolean[]> fm) {
        boolean[] mask = fm.apply(SPECIES.length());
        boolean[] r = fmr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < mask.length; i += SPECIES.length()) {
                VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, i);
                r[i] = vmask.allTrue();
            }
        }

        assertReductionBoolArraysEquals(mask, r, Long256VectorTests::allTrue);
    }


    @Test(dataProvider = "longUnaryOpProvider")
    static void withLong256VectorTests(IntFunction<long []> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.withLane(0, (long)4).intoArray(r, i);
            }
        }

        assertInsertArraysEquals(a, r, (long)4, 0);
    }
    static boolean testIS_DEFAULT(long a) {
        return bits(a)==0;
    }

    @Test(dataProvider = "longTestOpProvider")
    static void IS_DEFAULTLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                VectorMask<Long> mv = av.test(VectorOperators.IS_DEFAULT);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
   
                 Assert.assertEquals(mv.laneIsSet(j), testIS_DEFAULT(a[i + j]));
                }
            }
        }
    }

    static boolean testIS_NEGATIVE(long a) {
        return bits(a)<0;
    }

    @Test(dataProvider = "longTestOpProvider")
    static void IS_NEGATIVELong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                VectorMask<Long> mv = av.test(VectorOperators.IS_NEGATIVE);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
   
                 Assert.assertEquals(mv.laneIsSet(j), testIS_NEGATIVE(a[i + j]));
                }
            }
        }
    }





    @Test(dataProvider = "longCompareOpProvider")
    static void LTLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                VectorMask<Long> mv = av.compare(VectorOperators.LT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i + j]);
                }
            }
        }
    }


    @Test(dataProvider = "longCompareOpProvider")
    static void ltLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                VectorMask<Long> mv = av.lt(bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i + j]);
                }
            }
        }
    }


    @Test(dataProvider = "longCompareOpProvider")
    static void GTLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                VectorMask<Long> mv = av.compare(VectorOperators.GT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] > b[i + j]);
                }
            }
        }
    }


    @Test(dataProvider = "longCompareOpProvider")
    static void EQLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                VectorMask<Long> mv = av.compare(VectorOperators.EQ, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i + j]);
                }
            }
        }
    }


    @Test(dataProvider = "longCompareOpProvider")
    static void eqLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                VectorMask<Long> mv = av.eq(bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i + j]);
                }
            }
        }
    }


    @Test(dataProvider = "longCompareOpProvider")
    static void NELong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                VectorMask<Long> mv = av.compare(VectorOperators.NE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] != b[i + j]);
                }
            }
        }
    }


    @Test(dataProvider = "longCompareOpProvider")
    static void LELong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                VectorMask<Long> mv = av.compare(VectorOperators.LE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] <= b[i + j]);
                }
            }
        }
    }


    @Test(dataProvider = "longCompareOpProvider")
    static void GELong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                VectorMask<Long> mv = av.compare(VectorOperators.GE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] >= b[i + j]);
                }
            }
        }
    }


    static long blend(long a, long b, boolean mask) {
        return mask ? b : a;
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void blendLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.blend(bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::blend);
    }

    @Test(dataProvider = "longUnaryOpShuffleProvider")
    static void RearrangeLong256VectorTests(IntFunction<long[]> fa,
                                           BiFunction<Integer,Integer,int[]> fs) {
        long[] a = fa.apply(SPECIES.length());
        int[] order = fs.apply(a.length, SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.rearrange(VectorShuffle.fromArray(SPECIES, order, i)).intoArray(r, i);
            }
        }

        assertRearrangeArraysEquals(a, r, order, SPECIES.length());
    }




    @Test(dataProvider = "longUnaryOpProvider")
    static void getLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
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

        assertArraysEquals(a, r, Long256VectorTests::get);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void BroadcastLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = new long[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector.broadcast(SPECIES, a[i]).intoArray(r, i);
            }
        }

        assertBroadcastArraysEquals(a, r);
    }





    @Test(dataProvider = "longUnaryOpProvider")
    static void ZeroLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = new long[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector.zero(SPECIES).intoArray(a, i);
            }
        }

        Assert.assertEquals(a, r);
    }




    static long[] sliceUnary(long[] a, int origin, int idx) {
        long[] res = new long[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = a[idx+i+origin];
            else
                res[i] = (long)0;
        }
        return res;
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void sliceUnaryLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = new long[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.slice(origin).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, origin, Long256VectorTests::sliceUnary);
    }
    static long[] sliceBinary(long[] a, long[] b, int origin, int idx) {
        long[] res = new long[SPECIES.length()];
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

    @Test(dataProvider = "longBinaryOpProvider")
    static void sliceBinaryLong256VectorTestsBinary(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = new long[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.slice(origin, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, Long256VectorTests::sliceBinary);
    }
    static long[] slice(long[] a, long[] b, int origin, boolean[] mask, int idx) {
        long[] res = new long[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = mask[i] ? a[idx+i+origin] : (long)0;
            else {
                res[i] = mask[i] ? b[idx+j] : (long)0;
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void sliceLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
    IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        long[] r = new long[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.slice(origin, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, mask, Long256VectorTests::slice);
    }
    static long[] unsliceUnary(long[] a, int origin, int idx) {
        long[] res = new long[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i < origin)
                res[i] = (long)0;
            else {
                res[i] = a[idx+j];
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void unsliceUnaryLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = new long[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.unslice(origin).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, origin, Long256VectorTests::unsliceUnary);
    }
    static long[] unsliceBinary(long[] a, long[] b, int origin, int part, int idx) {
        long[] res = new long[SPECIES.length()];
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

    @Test(dataProvider = "longBinaryOpProvider")
    static void unsliceBinaryLong256VectorTestsBinary(IntFunction<long[]> fa, IntFunction<long[]> fb) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] r = new long[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        int part = (new java.util.Random()).nextInt(2);
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.unslice(origin, bv, part).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, part, Long256VectorTests::unsliceBinary);
    }
    static long[] unslice(long[] a, long[] b, int origin, int part, boolean[] mask, int idx) {
        long[] res = new long[SPECIES.length()];
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
        long[] res1 = new long[SPECIES.length()];
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

    @Test(dataProvider = "longBinaryOpMaskProvider")
    static void unsliceLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
    IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        long[] r = new long[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        int part = (new java.util.Random()).nextInt(2);
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                av.unslice(origin, bv, part, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, part, mask, Long256VectorTests::unslice);
    }






















    static long BITWISE_BLEND(long a, long b, long c) {
        return (long)((a&~(c))|(b&c));
    }
    static long bitwiseBlend(long a, long b, long c) {
        return (long)((a&~(c))|(b&c));
    }


    @Test(dataProvider = "longTernaryOpProvider")
    static void BITWISE_BLENDLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb, IntFunction<long[]> fc) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] c = fc.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                LongVector cv = LongVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.BITWISE_BLEND, bv, cv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, c, r, Long256VectorTests::BITWISE_BLEND);
    }
    @Test(dataProvider = "longTernaryOpProvider")
    static void bitwiseBlendLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb, IntFunction<long[]> fc) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] c = fc.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            LongVector cv = LongVector.fromArray(SPECIES, c, i);
            av.bitwiseBlend(bv, cv).intoArray(r, i);
        }

        assertArraysEquals(a, b, c, r, Long256VectorTests::bitwiseBlend);
    }


    @Test(dataProvider = "longTernaryOpMaskProvider")
    static void BITWISE_BLENDLong256VectorTestsMasked(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<long[]> fc, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] c = fc.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                LongVector bv = LongVector.fromArray(SPECIES, b, i);
                LongVector cv = LongVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.BITWISE_BLEND, bv, cv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, c, r, mask, Long256VectorTests::BITWISE_BLEND);
    }




    @Test(dataProvider = "longTernaryOpProvider")
    static void BITWISE_BLENDLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb, IntFunction<long[]> fc) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] c = fc.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, bv, c[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, c, r, Long256VectorTests::BITWISE_BLEND);
    }
    @Test(dataProvider = "longTernaryOpProvider")
    static void bitwiseBlendLong256VectorTestsBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb, IntFunction<long[]> fc) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] c = fc.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.bitwiseBlend(bv, c[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, c, r, Long256VectorTests::bitwiseBlend);
    }


    @Test(dataProvider = "longTernaryOpMaskProvider")
    static void BITWISE_BLENDLong256VectorTestsBroadcastMaskedSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<long[]> fc, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] c = fc.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            LongVector bv = LongVector.fromArray(SPECIES, b, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, bv, c[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, c, r, mask, Long256VectorTests::BITWISE_BLEND);
    }




    @Test(dataProvider = "longTernaryOpProvider")
    static void BITWISE_BLENDLong256VectorTestsDoubleBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb, IntFunction<long[]> fc) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] c = fc.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, b[i], c[i]).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(a, b, c, r, Long256VectorTests::BITWISE_BLEND);
    }
    @Test(dataProvider = "longTernaryOpProvider")
    static void bitwiseBlendLong256VectorTestsDoubleBroadcastSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb, IntFunction<long[]> fc) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] c = fc.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.bitwiseBlend(b[i], c[i]).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(a, b, c, r, Long256VectorTests::bitwiseBlend);
    }


    @Test(dataProvider = "longTernaryOpMaskProvider")
    static void BITWISE_BLENDLong256VectorTestsDoubleBroadcastMaskedSmokeTest(IntFunction<long[]> fa, IntFunction<long[]> fb,
                                          IntFunction<long[]> fc, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] b = fb.apply(SPECIES.length());
        long[] c = fc.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            LongVector av = LongVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, b[i], c[i], vmask).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(a, b, c, r, mask, Long256VectorTests::BITWISE_BLEND);
    }


    static long NEG(long a) {
        return (long)(-((long)a));
    }

    static long neg(long a) {
        return (long)(-((long)a));
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void NEGLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NEG).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Long256VectorTests::NEG);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void negLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.neg().intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Long256VectorTests::neg);
    }

    @Test(dataProvider = "longUnaryOpMaskProvider")
    static void NEGMaskedLong256VectorTests(IntFunction<long[]> fa,
                                                IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NEG, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, mask, Long256VectorTests::NEG);
    }

    static long ABS(long a) {
        return (long)(Math.abs((long)a));
    }

    static long abs(long a) {
        return (long)(Math.abs((long)a));
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void ABSLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ABS).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Long256VectorTests::ABS);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void absLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.abs().intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Long256VectorTests::abs);
    }

    @Test(dataProvider = "longUnaryOpMaskProvider")
    static void ABSMaskedLong256VectorTests(IntFunction<long[]> fa,
                                                IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ABS, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, mask, Long256VectorTests::ABS);
    }


    static long NOT(long a) {
        return (long)(~((long)a));
    }

    static long not(long a) {
        return (long)(~((long)a));
    }



    @Test(dataProvider = "longUnaryOpProvider")
    static void NOTLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NOT).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Long256VectorTests::NOT);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void notLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.not().intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Long256VectorTests::not);
    }



    @Test(dataProvider = "longUnaryOpMaskProvider")
    static void NOTMaskedLong256VectorTests(IntFunction<long[]> fa,
                                                IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NOT, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, mask, Long256VectorTests::NOT);
    }



    static long ZOMO(long a) {
        return (long)((a==0?0:-1));
    }



    @Test(dataProvider = "longUnaryOpProvider")
    static void ZOMOLong256VectorTests(IntFunction<long[]> fa) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ZOMO).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Long256VectorTests::ZOMO);
    }



    @Test(dataProvider = "longUnaryOpMaskProvider")
    static void ZOMOMaskedLong256VectorTests(IntFunction<long[]> fa,
                                                IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        long[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ZOMO, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, mask, Long256VectorTests::ZOMO);
    }




    static long[] gather(long a[], int ix, int[] b, int iy) {
        long[] res = new long[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++) {
            int bi = iy + i;
            res[i] = a[b[bi] + ix];
        }
        return res;
    }

    @Test(dataProvider = "longUnaryOpIndexProvider")
    static void gatherLong256VectorTests(IntFunction<long[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        long[] a = fa.apply(SPECIES.length());
        int[] b    = fs.apply(a.length, SPECIES.length());
        long[] r = new long[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i, b, i);
                av.intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::gather);
    }
    static long[] gatherMasked(long a[], int ix, boolean[] mask, int[] b, int iy) {
        long[] res = new long[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++) {
            int bi = iy + i;
            if (mask[i]) {
              res[i] = a[b[bi] + ix];
            }
        }
        return res;
    }

    @Test(dataProvider = "longUnaryMaskedOpIndexProvider")
    static void gatherMaskedLong256VectorTests(IntFunction<long[]> fa, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        int[] b    = fs.apply(a.length, SPECIES.length());
        long[] r = new long[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i, b, i, vmask);
                av.intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::gatherMasked);
    }

    static long[] scatter(long a[], int ix, int[] b, int iy) {
      long[] res = new long[SPECIES.length()];
      for (int i = 0; i < SPECIES.length(); i++) {
        int bi = iy + i;
        res[b[bi]] = a[i + ix];
      }
      return res;
    }

    @Test(dataProvider = "longUnaryOpIndexProvider")
    static void scatterLong256VectorTests(IntFunction<long[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        long[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        long[] r = new long[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, b, i);
            }
        }

        assertArraysEquals(a, b, r, Long256VectorTests::scatter);
    }

    static long[] scatterMasked(long r[], long a[], int ix, boolean[] mask, int[] b, int iy) {
      // First, gather r.
      long[] oldVal = gather(r, ix, b, iy);
      long[] newVal = new long[SPECIES.length()];

      // Second, blending it with a.
      for (int i = 0; i < SPECIES.length(); i++) {
        newVal[i] = blend(oldVal[i], a[i+ix], mask[i]);
      }

      // Third, scatter: copy old value of r, and scatter it manually.
      long[] res = Arrays.copyOfRange(r, ix, ix+SPECIES.length());
      for (int i = 0; i < SPECIES.length(); i++) {
        int bi = iy + i;
        res[b[bi]] = newVal[i];
      }

      return res;
    }

    @Test(dataProvider = "scatterMaskedOpIndexProvider")
    static void scatterMaskedLong256VectorTests(IntFunction<long[]> fa, IntFunction<long[]> fb, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        long[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        long[] r = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Long> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                LongVector av = LongVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, b, i, vmask);
            }
        }

        assertArraysEquals(a, b, r, mask, Long256VectorTests::scatterMasked);
    }

}

