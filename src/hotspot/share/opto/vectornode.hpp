/*
 * Copyright (c) 2007, 2014, Oracle and/or its affiliates. All rights reserved.
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

#ifndef SHARE_VM_OPTO_VECTORNODE_HPP
#define SHARE_VM_OPTO_VECTORNODE_HPP

#include "opto/matcher.hpp"
#include "opto/memnode.hpp"
#include "opto/node.hpp"
#include "opto/opcodes.hpp"
#include "opto/callnode.hpp"
#include "opto/subnode.hpp"

//------------------------------VectorNode-------------------------------------
// Vector Operation
class VectorNode : public TypeNode {
 public:

  VectorNode(Node* n1, const TypeVect* vt) : TypeNode(vt, 2) {
    init_class_id(Class_Vector);
    init_req(1, n1);
  }
  VectorNode(Node* n1, Node* n2, const TypeVect* vt) : TypeNode(vt, 3) {
    init_class_id(Class_Vector);
    init_req(1, n1);
    init_req(2, n2);
  }

  VectorNode(Node* n1, Node* n2, Node* n3, const TypeVect* vt) : TypeNode(vt, 4) {
    init_class_id(Class_Vector);
    init_req(1, n1);
    init_req(2, n2);
    init_req(3, n3);
  }

  const TypeVect* vect_type() const { return type()->is_vect(); }
  uint length() const { return vect_type()->length(); } // Vector length
  uint length_in_bytes() const { return vect_type()->length_in_bytes(); }

  virtual int Opcode() const;

  virtual uint ideal_reg() const { return Matcher::vector_ideal_reg(vect_type()->length_in_bytes()); }

  static VectorNode* scalar2vector(Node* s, uint vlen, const Type* opd_t);
  static VectorNode* shift_count(int opc, Node* cnt, uint vlen, BasicType bt);
  static VectorNode* make(int opc, Node* n1, Node* n2, uint vlen, BasicType bt);
  static VectorNode* make(int opc, Node* n1, Node* n2, Node* n3, uint vlen, BasicType bt);

  static int  opcode(int opc, BasicType bt);
  static int replicate_opcode(BasicType bt);
  static bool implemented(int opc, uint vlen, BasicType bt);
  static bool is_shift(Node* n);
  static bool is_invariant_vector(Node* n);
  // [Start, end) half-open range defining which operands are vectors
  static void vector_operands(Node* n, uint* start, uint* end);
};

//===========================Vector=ALU=Operations=============================

//------------------------------AddVBNode--------------------------------------
// Vector add byte
class AddVBNode : public VectorNode {
 public:
  AddVBNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------AddVSNode--------------------------------------
// Vector add char/short
class AddVSNode : public VectorNode {
 public:
  AddVSNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------AddVINode--------------------------------------
// Vector add int
class AddVINode : public VectorNode {
 public:
  AddVINode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------AddVLNode--------------------------------------
// Vector add long
class AddVLNode : public VectorNode {
public:
  AddVLNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------AddVFNode--------------------------------------
// Vector add float
class AddVFNode : public VectorNode {
public:
  AddVFNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------AddVDNode--------------------------------------
// Vector add double
class AddVDNode : public VectorNode {
public:
  AddVDNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------ReductionNode------------------------------------
// Perform reduction of a vector
class ReductionNode : public Node {
 public:
  ReductionNode(Node *ctrl, Node* in1, Node* in2) : Node(ctrl, in1, in2) {}

  static ReductionNode* make(int opc, Node *ctrl, Node* in1, Node* in2, BasicType bt);
  static int  opcode(int opc, BasicType bt);
  static bool implemented(int opc, uint vlen, BasicType bt);
  static Node* make_reduction_input(PhaseGVN& gvn, int opc, BasicType bt);
};

//------------------------------AddReductionVINode--------------------------------------
// Vector add byte, short and int as a reduction
class AddReductionVINode : public ReductionNode {
public:
  AddReductionVINode(Node * ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {
    if (in1->bottom_type()->basic_type() == T_INT) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_INT ||
             in2->bottom_type()->is_vect()->element_basic_type() == T_BYTE ||
             in2->bottom_type()->is_vect()->element_basic_type() == T_SHORT, "");
    }
  }
  virtual int Opcode() const;
  virtual const Type* bottom_type() const {
    if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_INT)
      return TypeInt::INT;
    else if(in(2)->bottom_type()->is_vect()->element_basic_type() == T_BYTE)
      return TypeInt::BYTE;
    else
      return TypeInt::SHORT;
  }
  virtual uint ideal_reg() const { return Op_RegI; }
};

//------------------------------AddReductionVLNode--------------------------------------
// Vector add long as a reduction
class AddReductionVLNode : public ReductionNode {
public:
  AddReductionVLNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {}
  virtual int Opcode() const;
  virtual const Type* bottom_type() const { return TypeLong::LONG; }
  virtual uint ideal_reg() const { return Op_RegL; }
};

//------------------------------AddReductionVFNode--------------------------------------
// Vector add float as a reduction
class AddReductionVFNode : public ReductionNode {
public:
  AddReductionVFNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {}
  virtual int Opcode() const;
  virtual const Type* bottom_type() const { return Type::FLOAT; }
  virtual uint ideal_reg() const { return Op_RegF; }
};

//------------------------------AddReductionVDNode--------------------------------------
// Vector add double as a reduction
class AddReductionVDNode : public ReductionNode {
public:
  AddReductionVDNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {}
  virtual int Opcode() const;
  virtual const Type* bottom_type() const { return Type::DOUBLE; }
  virtual uint ideal_reg() const { return Op_RegD; }
};

//------------------------------SubVBNode--------------------------------------
// Vector subtract byte
class SubVBNode : public VectorNode {
 public:
  SubVBNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------SubVSNode--------------------------------------
// Vector subtract short
class SubVSNode : public VectorNode {
 public:
  SubVSNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------SubVINode--------------------------------------
// Vector subtract int
class SubVINode : public VectorNode {
 public:
  SubVINode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------SubVLNode--------------------------------------
// Vector subtract long
class SubVLNode : public VectorNode {
 public:
  SubVLNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------SubVFNode--------------------------------------
// Vector subtract float
class SubVFNode : public VectorNode {
 public:
  SubVFNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------SubVDNode--------------------------------------
// Vector subtract double
class SubVDNode : public VectorNode {
 public:
  SubVDNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------SubReductionVNode--------------------------------------
// Vector sub int, long as a reduction
class SubReductionVNode : public ReductionNode {
public:
  SubReductionVNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {
    assert(in1->bottom_type()->basic_type() == in2->bottom_type()->is_vect()->element_basic_type(),"");
    assert(in1->bottom_type()->basic_type() == T_INT ||
           in1->bottom_type()->basic_type() == T_LONG, "");
  }
  virtual int Opcode() const;
  virtual Node *Ideal(PhaseGVN *phase, bool can_reshape);
  virtual const Type* bottom_type() const { if (in(1)->bottom_type()->basic_type() == T_INT)
                                              return TypeInt::INT; else return TypeLong::LONG; }
  virtual uint ideal_reg() const { return in(1)->bottom_type()->basic_type() == T_INT ? Op_RegI : Op_RegL; }
};


//------------------------------SubReductionVFPNode--------------------------------------
// Vector sub float, double as a reduction
class SubReductionVFPNode : public ReductionNode {
public:
  SubReductionVFPNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {
    assert(in1->bottom_type()->basic_type() == T_FLOAT ||
           in1->bottom_type()->basic_type() == T_DOUBLE, "");
  }

  virtual int Opcode() const;
  virtual const Type* bottom_type() const {
    if (in(1)->bottom_type()->basic_type() == T_FLOAT) {
      return Type::FLOAT;
    } else {
      return Type::DOUBLE;
    }
  }

  virtual uint ideal_reg() const { return in(1)->bottom_type()->basic_type() == T_FLOAT ? Op_RegF : Op_RegD; }
};

//------------------------------MulVBNode--------------------------------------
// Vector multiply byte
class MulVBNode : public VectorNode {
 public:
  MulVBNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------MulVSNode--------------------------------------
// Vector multiply short
class MulVSNode : public VectorNode {
 public:
  MulVSNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------MulVINode--------------------------------------
// Vector multiply int
class MulVINode : public VectorNode {
 public:
  MulVINode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------MulVLNode--------------------------------------
// Vector multiply long
class MulVLNode : public VectorNode {
public:
  MulVLNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------MulVFNode--------------------------------------
// Vector multiply float
class MulVFNode : public VectorNode {
public:
  MulVFNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------MulVDNode--------------------------------------
// Vector multiply double
class MulVDNode : public VectorNode {
public:
  MulVDNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------FmaVDNode--------------------------------------
// Vector multiply double
class FmaVDNode : public VectorNode {
public:
  FmaVDNode(Node* in1, Node* in2, Node* in3, const TypeVect* vt) : VectorNode(in1, in2, in3, vt) {}
  virtual int Opcode() const;
};

//------------------------------FmaVFNode--------------------------------------
// Vector multiply float
class FmaVFNode : public VectorNode {
public:
  FmaVFNode(Node* in1, Node* in2, Node* in3, const TypeVect* vt) : VectorNode(in1, in2, in3, vt) {}
  virtual int Opcode() const;
};

//------------------------------CMoveVFNode--------------------------------------
// Vector float conditional move
class CMoveVFNode : public VectorNode {
public:
  CMoveVFNode(Node* in1, Node* in2, Node* in3, const TypeVect* vt) : VectorNode(in1, in2, in3, vt) {}
  virtual int Opcode() const;
};

//------------------------------CMoveVDNode--------------------------------------
// Vector double conditional move
class CMoveVDNode : public VectorNode {
public:
  CMoveVDNode(Node* in1, Node* in2, Node* in3, const TypeVect* vt) : VectorNode(in1, in2, in3, vt) {}
  virtual int Opcode() const;
};

//------------------------------MulReductionVINode--------------------------------------
// Vector multiply byte, short and int as a reduction
class MulReductionVINode : public ReductionNode {
public:
  MulReductionVINode(Node * ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {
    if (in1->bottom_type()->basic_type() == T_INT) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_INT ||
        in2->bottom_type()->is_vect()->element_basic_type() == T_BYTE ||
        in2->bottom_type()->is_vect()->element_basic_type() == T_SHORT, "");
    }
  }
  virtual int Opcode() const;
  virtual const Type* bottom_type() const {
    if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_INT)
      return TypeInt::INT;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_BYTE)
      return TypeInt::BYTE;
    else
      return TypeInt::SHORT;
  }
  virtual uint ideal_reg() const { return Op_RegI; }
};

//------------------------------MulReductionVLNode--------------------------------------
// Vector multiply int as a reduction
class MulReductionVLNode : public ReductionNode {
public:
  MulReductionVLNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {}
  virtual int Opcode() const;
  virtual const Type* bottom_type() const { return TypeLong::LONG; }
  virtual uint ideal_reg() const { return Op_RegL; }
};

//------------------------------MulReductionVFNode--------------------------------------
// Vector multiply float as a reduction
class MulReductionVFNode : public ReductionNode {
public:
  MulReductionVFNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {}
  virtual int Opcode() const;
  virtual const Type* bottom_type() const { return Type::FLOAT; }
  virtual uint ideal_reg() const { return Op_RegF; }
};

//------------------------------MulReductionVDNode--------------------------------------
// Vector multiply double as a reduction
class MulReductionVDNode : public ReductionNode {
public:
  MulReductionVDNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {}
  virtual int Opcode() const;
  virtual const Type* bottom_type() const { return Type::DOUBLE; }
  virtual uint ideal_reg() const { return Op_RegD; }
};

//------------------------------DivVFNode--------------------------------------
// Vector divide float
class DivVFNode : public VectorNode {
 public:
  DivVFNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------DivVDNode--------------------------------------
// Vector Divide double
class DivVDNode : public VectorNode {
 public:
  DivVDNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------MinVNode--------------------------------------
// Vector Min
class MinVNode : public VectorNode {
public:
  MinVNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------MaxVNode--------------------------------------
// Vector Max
class MaxVNode : public VectorNode {
public:
  MaxVNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------AbsVNode--------------------------------------
// Vector Abs
class AbsVNode : public VectorNode {
public:
  AbsVNode(Node* in, const TypeVect* vt) : VectorNode(in, vt) {}
  virtual int Opcode() const;
};

//------------------------------AbsVFNode--------------------------------------
// Vector Abs float
class AbsVFNode : public VectorNode {
 public:
  AbsVFNode(Node* in, const TypeVect* vt) : VectorNode(in,vt) {}
  virtual int Opcode() const;
};

//------------------------------AbsVDNode--------------------------------------
// Vector Abs double
class AbsVDNode : public VectorNode {
 public:
  AbsVDNode(Node* in, const TypeVect* vt) : VectorNode(in,vt) {}
  virtual int Opcode() const;
};

//------------------------------NegVINode--------------------------------------
// Vector Neg int
class NegVINode : public VectorNode {
public:
  NegVINode(Node* in, const TypeVect* vt) : VectorNode(in, vt) {}
  virtual int Opcode() const;
};

//------------------------------NegVFNode--------------------------------------
// Vector Neg float
class NegVFNode : public VectorNode {
 public:
  NegVFNode(Node* in, const TypeVect* vt) : VectorNode(in,vt) {}
  virtual int Opcode() const;
};

//------------------------------NegVDNode--------------------------------------
// Vector Neg double
class NegVDNode : public VectorNode {
 public:
  NegVDNode(Node* in, const TypeVect* vt) : VectorNode(in,vt) {}
  virtual int Opcode() const;
};

//------------------------------PopCountVINode---------------------------------
// Vector popcount integer bits
class PopCountVINode : public VectorNode {
 public:
  PopCountVINode(Node* in, const TypeVect* vt) : VectorNode(in,vt) {}
  virtual int Opcode() const;
};

//------------------------------SqrtVFNode--------------------------------------
// Vector Sqrt float
class SqrtVFNode : public VectorNode {
 public:
  SqrtVFNode(Node* in, const TypeVect* vt) : VectorNode(in,vt) {}
  virtual int Opcode() const;
};

//------------------------------SqrtVDNode--------------------------------------
// Vector Sqrt double
class SqrtVDNode : public VectorNode {
 public:
  SqrtVDNode(Node* in, const TypeVect* vt) : VectorNode(in,vt) {}
  virtual int Opcode() const;
};

//------------------------------NotVNode--------------------------------------
// Vector Not
class NotVNode : public VectorNode {
public:
  NotVNode(Node* in, const TypeVect* vt) : VectorNode(in, vt) {}
  virtual int Opcode() const;
};

//------------------------------LShiftVBNode-----------------------------------
// Vector left shift bytes
class LShiftVBNode : public VectorNode {
 public:
  LShiftVBNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------LShiftVSNode-----------------------------------
// Vector left shift shorts
class LShiftVSNode : public VectorNode {
 public:
  LShiftVSNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------LShiftVINode-----------------------------------
// Vector left shift ints
class LShiftVINode : public VectorNode {
 public:
  LShiftVINode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------LShiftVLNode-----------------------------------
// Vector left shift longs
class LShiftVLNode : public VectorNode {
 public:
  LShiftVLNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------RShiftVBNode-----------------------------------
// Vector right arithmetic (signed) shift bytes
class RShiftVBNode : public VectorNode {
 public:
  RShiftVBNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------RShiftVSNode-----------------------------------
// Vector right arithmetic (signed) shift shorts
class RShiftVSNode : public VectorNode {
 public:
  RShiftVSNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------RShiftVINode-----------------------------------
// Vector right arithmetic (signed) shift ints
class RShiftVINode : public VectorNode {
 public:
  RShiftVINode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------RShiftVLNode-----------------------------------
// Vector right arithmetic (signed) shift longs
class RShiftVLNode : public VectorNode {
 public:
  RShiftVLNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------URShiftVBNode----------------------------------
// Vector right logical (unsigned) shift bytes
class URShiftVBNode : public VectorNode {
 public:
  URShiftVBNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------URShiftVSNode----------------------------------
// Vector right logical (unsigned) shift shorts
class URShiftVSNode : public VectorNode {
 public:
  URShiftVSNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------URShiftVINode----------------------------------
// Vector right logical (unsigned) shift ints
class URShiftVINode : public VectorNode {
 public:
  URShiftVINode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------URShiftVLNode----------------------------------
// Vector right logical (unsigned) shift longs
class URShiftVLNode : public VectorNode {
 public:
  URShiftVLNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------LShiftCntVNode---------------------------------
// Vector left shift count
class LShiftCntVNode : public VectorNode {
 public:
  LShiftCntVNode(Node* cnt, const TypeVect* vt) : VectorNode(cnt,vt) {}
  virtual int Opcode() const;
  virtual uint ideal_reg() const { return Matcher::vector_shift_count_ideal_reg(vect_type()->length_in_bytes()); }
};

//------------------------------RShiftCntVNode---------------------------------
// Vector right shift count
class RShiftCntVNode : public VectorNode {
 public:
  RShiftCntVNode(Node* cnt, const TypeVect* vt) : VectorNode(cnt,vt) {}
  virtual int Opcode() const;
  virtual uint ideal_reg() const { return Matcher::vector_shift_count_ideal_reg(vect_type()->length_in_bytes()); }
};


//------------------------------AndVNode---------------------------------------
// Vector and integer
class AndVNode : public VectorNode {
 public:
  AndVNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------AndReductionVNode--------------------------------------
// Vector and byte, short, int, long as a reduction
class AndReductionVNode : public ReductionNode {
public:
  AndReductionVNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {
    if (in1->bottom_type()->basic_type() == T_INT) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_INT ||
        in2->bottom_type()->is_vect()->element_basic_type() == T_BYTE ||
        in2->bottom_type()->is_vect()->element_basic_type() == T_SHORT, "");
    }
    else if (in1->bottom_type()->basic_type() == T_LONG) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_LONG, "");
    }
  }
  virtual int Opcode() const;
  virtual const Type* bottom_type() const {
    if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_INT)
      return TypeInt::INT;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_BYTE)
      return TypeInt::BYTE;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_SHORT)
      return TypeInt::SHORT;
    else
      return TypeLong::LONG;
  }
  virtual uint ideal_reg() const { return in(1)->bottom_type()->basic_type() == T_INT ? Op_RegI : Op_RegL; }
};

//------------------------------OrVNode---------------------------------------
// Vector or integer
class OrVNode : public VectorNode {
 public:
  OrVNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------OrReductionVNode--------------------------------------
// Vector or short, byte, int, long as a reduction
class OrReductionVNode : public ReductionNode {
public:
  OrReductionVNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {
    assert(in1->bottom_type()->basic_type() == T_INT ||
           in1->bottom_type()->basic_type() == T_LONG ||
           in1->bottom_type()->basic_type() == T_SHORT ||
           in1->bottom_type()->basic_type() == T_BYTE, "");
  }
  virtual int Opcode() const;
  virtual const Type* bottom_type() const {
    if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_INT)
      return TypeInt::INT;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_BYTE)
      return TypeInt::BYTE;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_SHORT)
      return TypeInt::SHORT;
    else
      return TypeLong::LONG;
  }

  virtual uint ideal_reg() const { return in(1)->bottom_type()->basic_type() == T_INT ? Op_RegI : Op_RegL; }
};

//------------------------------XorReductionVNode--------------------------------------
// Vector and int, long as a reduction
class XorReductionVNode : public ReductionNode {
public:
  XorReductionVNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {
    assert(in1->bottom_type()->basic_type() == T_INT ||
           in1->bottom_type()->basic_type() == T_LONG ||
           in1->bottom_type()->basic_type() == T_SHORT ||
           in1->bottom_type()->basic_type() == T_BYTE, "");
  }
  virtual int Opcode() const;
  virtual const Type* bottom_type() const {
    if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_INT)
      return TypeInt::INT;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_BYTE)
      return TypeInt::BYTE;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_SHORT)
      return TypeInt::SHORT;
    else
      return TypeLong::LONG;
  }

  virtual uint ideal_reg() const { return in(1)->bottom_type()->basic_type() == T_INT ? Op_RegI : Op_RegL; }
};

//------------------------------XorVNode---------------------------------------
// Vector xor integer
class XorVNode : public VectorNode {
 public:
  XorVNode(Node* in1, Node* in2, const TypeVect* vt) : VectorNode(in1,in2,vt) {}
  virtual int Opcode() const;
};

//------------------------------MinReductionVNode--------------------------------------
// Vector min byte, short, int, long, float, double as a reduction
class MinReductionVNode : public ReductionNode {
public:
  MinReductionVNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {
    if (in1->bottom_type()->basic_type() == T_INT) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_INT ||
        in2->bottom_type()->is_vect()->element_basic_type() == T_BYTE ||
        in2->bottom_type()->is_vect()->element_basic_type() == T_SHORT, "");
    }
    else if (in1->bottom_type()->basic_type() == T_LONG) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_LONG, "");
    }
    else if (in1->bottom_type()->basic_type() == T_FLOAT) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_FLOAT, "");
    }
    else if (in1->bottom_type()->basic_type() == T_DOUBLE) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_DOUBLE, "");
    }
  }
  virtual int Opcode() const;
  virtual const Type* bottom_type() const {  
    if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_INT)
      return TypeInt::INT;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_BYTE)
      return TypeInt::BYTE;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_SHORT)
      return TypeInt::SHORT;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_FLOAT)
      return Type::FLOAT;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_DOUBLE)
      return Type::DOUBLE;
		else return TypeLong::LONG;
	}
  virtual uint ideal_reg() const {
    if (in(1)->bottom_type()->basic_type() == T_INT)
      return Op_RegI;
    else if (in(1)->bottom_type()->basic_type() == T_FLOAT)
      return Op_RegF;
    else if (in(1)->bottom_type()->basic_type() == T_DOUBLE)
      return Op_RegD;
    else return Op_RegL;
  }
};

//------------------------------MaxReductionVNode--------------------------------------
// Vector min byte, short, int, long, float, double as a reduction
class MaxReductionVNode : public ReductionNode {
public:
  MaxReductionVNode(Node *ctrl, Node* in1, Node* in2) : ReductionNode(ctrl, in1, in2) {
    if (in1->bottom_type()->basic_type() == T_INT) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_INT ||
        in2->bottom_type()->is_vect()->element_basic_type() == T_BYTE ||
        in2->bottom_type()->is_vect()->element_basic_type() == T_SHORT, "");
    }
    else if (in1->bottom_type()->basic_type() == T_LONG) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_LONG, "");
    }
    else if (in1->bottom_type()->basic_type() == T_FLOAT) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_FLOAT, "");
    }
    else if (in1->bottom_type()->basic_type() == T_DOUBLE) {
      assert(in2->bottom_type()->is_vect()->element_basic_type() == T_DOUBLE, "");
    }
  }
  virtual int Opcode() const;
  virtual const Type* bottom_type() const {
    if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_INT)
      return TypeInt::INT;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_BYTE)
      return TypeInt::BYTE;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_SHORT)
      return TypeInt::SHORT;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_FLOAT)
      return Type::FLOAT;
    else if (in(2)->bottom_type()->is_vect()->element_basic_type() == T_DOUBLE)
      return Type::DOUBLE;
    else return TypeLong::LONG;
  }
  virtual uint ideal_reg() const {
    if (in(1)->bottom_type()->basic_type() == T_INT)
      return Op_RegI;
    else if (in(1)->bottom_type()->basic_type() == T_FLOAT)
      return Op_RegF;
    else if (in(1)->bottom_type()->basic_type() == T_DOUBLE)
      return Op_RegD;
    else return Op_RegL;
  }
};

//================================= M E M O R Y ===============================

//------------------------------LoadVectorNode---------------------------------
// Load Vector from memory
class LoadVectorNode : public LoadNode {
 public:
  LoadVectorNode(Node* c, Node* mem, Node* adr, const TypePtr* at, const TypeVect* vt, ControlDependency control_dependency = LoadNode::DependsOnlyOnTest)
    : LoadNode(c, mem, adr, at, vt, MemNode::unordered, control_dependency) {
    init_class_id(Class_LoadVector);
  }

  const TypeVect* vect_type() const { return type()->is_vect(); }
  uint length() const { return vect_type()->length(); } // Vector length

  virtual int Opcode() const;

  virtual uint ideal_reg() const  { return Matcher::vector_ideal_reg(memory_size()); }
  virtual BasicType memory_type() const { return T_VOID; }
  virtual int memory_size() const { return vect_type()->length_in_bytes(); }

  virtual int store_Opcode() const { return Op_StoreVector; }

  static LoadVectorNode* make(int opc, Node* ctl, Node* mem,
                              Node* adr, const TypePtr* atyp,
                              uint vlen, BasicType bt,
                              ControlDependency control_dependency = LoadNode::DependsOnlyOnTest);
  uint element_size(void) { return type2aelembytes(vect_type()->element_basic_type()); }
};

//------------------------------StoreVectorNode--------------------------------
// Store Vector to memory
class StoreVectorNode : public StoreNode {
 public:
  StoreVectorNode(Node* c, Node* mem, Node* adr, const TypePtr* at, Node* val)
    : StoreNode(c, mem, adr, at, val, MemNode::unordered) {
    init_class_id(Class_StoreVector);
  }

  const TypeVect* vect_type() const { return in(MemNode::ValueIn)->bottom_type()->is_vect(); }
  uint length() const { return vect_type()->length(); } // Vector length

  virtual int Opcode() const;

  virtual uint ideal_reg() const  { return Matcher::vector_ideal_reg(memory_size()); }
  virtual BasicType memory_type() const { return T_VOID; }
  virtual int memory_size() const { return vect_type()->length_in_bytes(); }

  static StoreVectorNode* make(int opc, Node* ctl, Node* mem,
                               Node* adr, const TypePtr* atyp, Node* val,
                               uint vlen);

  uint element_size(void) { return type2aelembytes(vect_type()->element_basic_type()); }
};


//=========================Promote_Scalar_to_Vector============================

//------------------------------ReplicateBNode---------------------------------
// Replicate byte scalar to be vector
class ReplicateBNode : public VectorNode {
 public:
  ReplicateBNode(Node* in1, const TypeVect* vt) : VectorNode(in1, vt) {}
  virtual int Opcode() const;
};

//------------------------------ReplicateSNode---------------------------------
// Replicate short scalar to be vector
class ReplicateSNode : public VectorNode {
 public:
  ReplicateSNode(Node* in1, const TypeVect* vt) : VectorNode(in1, vt) {}
  virtual int Opcode() const;
};

//------------------------------ReplicateINode---------------------------------
// Replicate int scalar to be vector
class ReplicateINode : public VectorNode {
 public:
  ReplicateINode(Node* in1, const TypeVect* vt) : VectorNode(in1, vt) {}
  virtual int Opcode() const;
};

//------------------------------ReplicateLNode---------------------------------
// Replicate long scalar to be vector
class ReplicateLNode : public VectorNode {
 public:
  ReplicateLNode(Node* in1, const TypeVect* vt) : VectorNode(in1, vt) {}
  virtual int Opcode() const;
};

//------------------------------ReplicateFNode---------------------------------
// Replicate float scalar to be vector
class ReplicateFNode : public VectorNode {
 public:
  ReplicateFNode(Node* in1, const TypeVect* vt) : VectorNode(in1, vt) {}
  virtual int Opcode() const;
};

//------------------------------ReplicateDNode---------------------------------
// Replicate double scalar to be vector
class ReplicateDNode : public VectorNode {
 public:
  ReplicateDNode(Node* in1, const TypeVect* vt) : VectorNode(in1, vt) {}
  virtual int Opcode() const;
};

//========================Pack_Scalars_into_a_Vector===========================

//------------------------------PackNode---------------------------------------
// Pack parent class (not for code generation).
class PackNode : public VectorNode {
 public:
  PackNode(Node* in1, const TypeVect* vt) : VectorNode(in1, vt) {}
  PackNode(Node* in1, Node* n2, const TypeVect* vt) : VectorNode(in1, n2, vt) {}
  virtual int Opcode() const;

  void add_opd(Node* n) {
    add_req(n);
  }

  // Create a binary tree form for Packs. [lo, hi) (half-open) range
  PackNode* binary_tree_pack(int lo, int hi);

  static PackNode* make(Node* s, uint vlen, BasicType bt);
};

//------------------------------PackBNode--------------------------------------
// Pack byte scalars into vector
class PackBNode : public PackNode {
 public:
  PackBNode(Node* in1, const TypeVect* vt)  : PackNode(in1, vt) {}
  virtual int Opcode() const;
};

//------------------------------PackSNode--------------------------------------
// Pack short scalars into a vector
class PackSNode : public PackNode {
 public:
  PackSNode(Node* in1, const TypeVect* vt)  : PackNode(in1, vt) {}
  PackSNode(Node* in1, Node* in2, const TypeVect* vt) : PackNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------PackINode--------------------------------------
// Pack integer scalars into a vector
class PackINode : public PackNode {
 public:
  PackINode(Node* in1, const TypeVect* vt)  : PackNode(in1, vt) {}
  PackINode(Node* in1, Node* in2, const TypeVect* vt) : PackNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------PackLNode--------------------------------------
// Pack long scalars into a vector
class PackLNode : public PackNode {
 public:
  PackLNode(Node* in1, const TypeVect* vt)  : PackNode(in1, vt) {}
  PackLNode(Node* in1, Node* in2, const TypeVect* vt) : PackNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------Pack2LNode-------------------------------------
// Pack 2 long scalars into a vector
class Pack2LNode : public PackNode {
 public:
  Pack2LNode(Node* in1, Node* in2, const TypeVect* vt) : PackNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------PackFNode--------------------------------------
// Pack float scalars into vector
class PackFNode : public PackNode {
 public:
  PackFNode(Node* in1, const TypeVect* vt)  : PackNode(in1, vt) {}
  PackFNode(Node* in1, Node* in2, const TypeVect* vt) : PackNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------PackDNode--------------------------------------
// Pack double scalars into a vector
class PackDNode : public PackNode {
 public:
  PackDNode(Node* in1, const TypeVect* vt) : PackNode(in1, vt) {}
  PackDNode(Node* in1, Node* in2, const TypeVect* vt) : PackNode(in1, in2, vt) {}
  virtual int Opcode() const;
};

//------------------------------Pack2DNode-------------------------------------
// Pack 2 double scalars into a vector
class Pack2DNode : public PackNode {
 public:
  Pack2DNode(Node* in1, Node* in2, const TypeVect* vt) : PackNode(in1, in2, vt) {}
  virtual int Opcode() const;
};


//========================Extract_Scalar_from_Vector===========================

//------------------------------ExtractNode------------------------------------
// Extract a scalar from a vector at position "pos"
class ExtractNode : public Node {
 public:
  ExtractNode(Node* src, ConINode* pos) : Node(NULL, src, (Node*)pos) {
    assert(in(2)->get_int() >= 0, "positive constants");
  }
  virtual int Opcode() const;
  uint  pos() const { return in(2)->get_int(); }

  static Node* make(Node* v, uint position, BasicType bt);
  static int opcode(BasicType bt);
};

//------------------------------ExtractBNode-----------------------------------
// Extract a byte from a vector at position "pos"
class ExtractBNode : public ExtractNode {
 public:
  ExtractBNode(Node* src, ConINode* pos) : ExtractNode(src, pos) {}
  virtual int Opcode() const;
  virtual const Type *bottom_type() const { return TypeInt::INT; }
  virtual uint ideal_reg() const { return Op_RegI; }
};

//------------------------------ExtractUBNode----------------------------------
// Extract a boolean from a vector at position "pos"
class ExtractUBNode : public ExtractNode {
 public:
  ExtractUBNode(Node* src, ConINode* pos) : ExtractNode(src, pos) {}
  virtual int Opcode() const;
  virtual const Type *bottom_type() const { return TypeInt::INT; }
  virtual uint ideal_reg() const { return Op_RegI; }
};

//------------------------------ExtractCNode-----------------------------------
// Extract a char from a vector at position "pos"
class ExtractCNode : public ExtractNode {
 public:
  ExtractCNode(Node* src, ConINode* pos) : ExtractNode(src, pos) {}
  virtual int Opcode() const;
  virtual const Type *bottom_type() const { return TypeInt::CHAR; }
  virtual uint ideal_reg() const { return Op_RegI; }
};

//------------------------------ExtractSNode-----------------------------------
// Extract a short from a vector at position "pos"
class ExtractSNode : public ExtractNode {
 public:
  ExtractSNode(Node* src, ConINode* pos) : ExtractNode(src, pos) {}
  virtual int Opcode() const;
  virtual const Type *bottom_type() const { return TypeInt::SHORT; }
  virtual uint ideal_reg() const { return Op_RegI; }
};

//------------------------------ExtractINode-----------------------------------
// Extract an int from a vector at position "pos"
class ExtractINode : public ExtractNode {
 public:
  ExtractINode(Node* src, ConINode* pos) : ExtractNode(src, pos) {}
  virtual int Opcode() const;
  virtual const Type *bottom_type() const { return TypeInt::INT; }
  virtual uint ideal_reg() const { return Op_RegI; }
};

//------------------------------ExtractLNode-----------------------------------
// Extract a long from a vector at position "pos"
class ExtractLNode : public ExtractNode {
 public:
  ExtractLNode(Node* src, ConINode* pos) : ExtractNode(src, pos) {}
  virtual int Opcode() const;
  virtual const Type *bottom_type() const { return TypeLong::LONG; }
  virtual uint ideal_reg() const { return Op_RegL; }
};

//------------------------------ExtractFNode-----------------------------------
// Extract a float from a vector at position "pos"
class ExtractFNode : public ExtractNode {
 public:
  ExtractFNode(Node* src, ConINode* pos) : ExtractNode(src, pos) {}
  virtual int Opcode() const;
  virtual const Type *bottom_type() const { return Type::FLOAT; }
  virtual uint ideal_reg() const { return Op_RegF; }
};

//------------------------------ExtractDNode-----------------------------------
// Extract a double from a vector at position "pos"
class ExtractDNode : public ExtractNode {
 public:
  ExtractDNode(Node* src, ConINode* pos) : ExtractNode(src, pos) {}
  virtual int Opcode() const;
  virtual const Type *bottom_type() const { return Type::DOUBLE; }
  virtual uint ideal_reg() const { return Op_RegD; }
};

//------------------------------SetVectMaskINode-------------------------------
// Provide a mask for a vector predicate machine
class SetVectMaskINode : public Node {
public:
  SetVectMaskINode(Node *c, Node *in1) : Node(c, in1) {}
  virtual int Opcode() const;
  const Type *bottom_type() const { return TypeInt::INT; }
  virtual uint ideal_reg() const { return Op_RegI; }
  virtual const Type *Value(PhaseGVN *phase) const { return TypeInt::INT; }
};

class VectorBoxNode : public Node {
 private:
  const TypeInstPtr* const _box_type;
  const TypeVect*    const _vec_type;
 public:
  enum {
     Box   = 1,
     Value = 2
  };
  VectorBoxNode(Compile* C, Node* box, Node* val,
                const TypeInstPtr* box_type, const TypeVect* vt)
    : Node(NULL, box, val), _box_type(box_type), _vec_type(vt) {
    init_flags(Flag_is_macro);
    C->add_macro_node(this);
  }

  const  TypeInstPtr* box_type() const { assert(_box_type != NULL, ""); return _box_type; };
  const  TypeVect*    vec_type() const { assert(_vec_type != NULL, ""); return _vec_type; };

  virtual int Opcode() const;
  virtual const Type *bottom_type() const { return _box_type; /* TypeInstPtr::BOTTOM? */ }
  virtual       uint  ideal_reg() const { return box_type()->ideal_reg(); }
  virtual       uint  size_of() const { return sizeof(*this); }

  static const TypeFunc* vec_box_type(const TypeInstPtr* box_type);
};

class VectorBoxAllocateNode : public CallStaticJavaNode {
 public:
  VectorBoxAllocateNode(Compile* C, const TypeInstPtr* vbox_type)
    : CallStaticJavaNode(C, VectorBoxNode::vec_box_type(vbox_type), NULL, NULL, -1) {
    init_flags(Flag_is_macro);
    C->add_macro_node(this);
  }

  virtual int Opcode() const;
#ifndef PRODUCT
  virtual void dump_spec(outputStream *st) const;
#endif // PRODUCT
};

class VectorUnboxNode : public VectorNode {
 public:
  VectorUnboxNode(Compile* C, const TypeVect* vec_type, Node* obj, Node* mem)
    : VectorNode(mem, obj, vec_type) {
    init_flags(Flag_is_macro);
    C->add_macro_node(this);
  }

  virtual int Opcode() const;
  Node* obj() const { return in(2); }
  Node* mem() const { return in(1); }
  virtual Node *Identity(PhaseGVN *phase);
};

class VectorMaskCmpNode : public VectorNode {
 private:
  BoolTest::mask _predicate;

 protected:
  uint size_of() const { return sizeof(*this); }

 public:
  VectorMaskCmpNode(BoolTest::mask predicate, Node* in1, Node* in2, const TypeVect* vt) :
      VectorNode(in1, in2, vt), _predicate(predicate) {
    assert(in1->bottom_type()->is_vect()->element_basic_type() == in2->bottom_type()->is_vect()->element_basic_type(),
           "VectorMaskCmp inputs must have same type for elements");
    assert(in1->bottom_type()->is_vect()->length() == in2->bottom_type()->is_vect()->length(),
           "VectorMaskCmp inputs must have same number of elements");
    init_class_id(Class_VectorMaskCmp);
  }

  virtual int Opcode() const;
  virtual uint hash() const { return VectorNode::hash() + _predicate; }
  virtual uint cmp( const Node &n ) const {
    return VectorNode::cmp(n) && _predicate == ((VectorMaskCmpNode&)n)._predicate;
  }
  BoolTest::mask get_predicate() { return _predicate; }
#ifndef PRODUCT
  virtual void dump_spec(outputStream *st) const;
#endif // PRODUCT
};

// Used to wrap other vector nodes in order to add masking functionality.
class VectorMaskWrapperNode : public VectorNode {
public:
  VectorMaskWrapperNode(Node* vector, Node* mask)
    : VectorNode(vector, mask, vector->bottom_type()->is_vect()) {
    assert(mask->is_VectorMaskCmp(), "VectorMaskWrapper requires that second argument be a mask");
  }

  virtual int Opcode() const;
  Node* vector_val() const { return in(1); }
  Node* vector_mask() const { return in(2); }
};

class VectorTestNode : public Node {
 private:
  Assembler::Condition _predicate;

 protected:
  uint size_of() const { return sizeof(*this); }

 public:
  VectorTestNode( Node *in1, Node *in2, Assembler::Condition predicate) : Node(NULL, in1, in2), _predicate(predicate) {
    assert(in1->is_Vector() || in1->is_LoadVector(), "must be vector");
    assert(in2->is_Vector() || in2->is_LoadVector(), "must be vector");
    assert(in1->bottom_type()->is_vect()->element_basic_type() == in2->bottom_type()->is_vect()->element_basic_type(),
           "same type elements are needed");
    assert(in1->bottom_type()->is_vect()->length() == in2->bottom_type()->is_vect()->length(),
           "same number of elements is needed");
  }
  virtual int Opcode() const;
  virtual uint hash() const { return Node::hash() + _predicate; }
  virtual uint cmp( const Node &n ) const {
    return Node::cmp(n) && _predicate == ((VectorTestNode&)n)._predicate;
  }
  virtual const Type *bottom_type() const { return TypeInt::BOOL; }
  virtual uint ideal_reg() const { return Op_RegI; }  // TODO Should be RegFlags but due to missing comparison flags for BoolTest
                                                      // in middle-end, we make it boolean result directly.
  Assembler::Condition get_predicate() const { return _predicate; }
};

class VectorBlendNode : public VectorNode {
public:
  VectorBlendNode(Node* vec1, Node* vec2, Node* mask)
    : VectorNode(vec1, vec2, mask, vec1->bottom_type()->is_vect()) {
    // assert(mask->is_VectorMask(), "VectorBlendNode requires that third argument be a mask");
  }

  virtual int Opcode() const;
  Node* vec1() const { return in(1); }
  Node* vec2() const { return in(2); }
  Node* vec_mask() const { return in(3); }
};

class VectorLoadMaskNode : public VectorNode {
 public:
  VectorLoadMaskNode(Node* in, const TypeVect* vt)
    : VectorNode(in, vt) {
    assert(in->is_LoadVector(), "expected load vector");
    assert(in->as_LoadVector()->vect_type()->element_basic_type() == T_BOOLEAN, "must be boolean");
  }

  int GetOutMaskSize() const { return type2aelembytes(vect_type()->element_basic_type()); }
  virtual int Opcode() const;
};

class VectorStoreMaskNode : public VectorNode {
 private:
  int _mask_size;
 protected:
  uint size_of() const { return sizeof(*this); }

 public:
  VectorStoreMaskNode(Node* in, BasicType in_type, uint num_elem)
    : VectorNode(in, TypeVect::make(T_BOOLEAN, num_elem)) {
    _mask_size = type2aelembytes(in_type);
  }

  virtual uint hash() const { return VectorNode::hash() + _mask_size; }
  virtual uint cmp( const Node &n ) const {
    return VectorNode::cmp(n) && _mask_size == ((VectorStoreMaskNode&)n)._mask_size;
  }
  int GetInputMaskSize() const { return _mask_size; }
  virtual int Opcode() const;
};

// This is intended for use as a simple reinterpret node that has no cast.
class VectorReinterpretNode : public VectorNode {
 private:
  const TypeVect* _src_vt;
 protected:
  uint size_of() const { return sizeof(*this); }
 public:
  VectorReinterpretNode(Node* in, const TypeVect* src_vt, const TypeVect* dst_vt)
      : VectorNode(in, dst_vt), _src_vt(src_vt) { }

  virtual uint hash() const { return VectorNode::hash() + _src_vt->hash(); }
  virtual uint cmp( const Node &n ) const {
    return VectorNode::cmp(n) && !Type::cmp(_src_vt,((VectorReinterpretNode&)n)._src_vt);
  }
  virtual Node *Identity(PhaseGVN *phase);

  virtual int Opcode() const;
};

class VectorCastNode : public VectorNode {
 public:
  VectorCastNode(Node* in, const TypeVect* vt) : VectorNode(in, vt) {}
  virtual int Opcode() const;

  static VectorCastNode* make(int vopc, Node* n1, BasicType bt, uint vlen);
  static int  opcode(BasicType bt);
  static bool implemented(BasicType bt, uint vlen);
};

class VectorCastB2XNode : public VectorCastNode {
public:
  VectorCastB2XNode(Node* in, const TypeVect* vt) : VectorCastNode(in, vt) {
    assert(in->bottom_type()->is_vect()->element_basic_type() == T_BYTE, "must be byte");
  }
  virtual int Opcode() const;
};

class VectorCastS2XNode : public VectorCastNode {
public:
  VectorCastS2XNode(Node* in, const TypeVect* vt) : VectorCastNode(in, vt) {
    assert(in->bottom_type()->is_vect()->element_basic_type() == T_SHORT, "must be short");
  }
  virtual int Opcode() const;
};

class VectorCastI2XNode : public VectorCastNode {
public:
  VectorCastI2XNode(Node* in, const TypeVect* vt) : VectorCastNode(in, vt) {
    assert(in->bottom_type()->is_vect()->element_basic_type() == T_INT, "must be int");
  }
  virtual int Opcode() const;
};

class VectorCastL2XNode : public VectorCastNode {
public:
  VectorCastL2XNode(Node* in, const TypeVect* vt) : VectorCastNode(in, vt) {
    assert(in->bottom_type()->is_vect()->element_basic_type() == T_LONG, "must be long");
  }
  virtual int Opcode() const;
};

class VectorCastF2XNode : public VectorCastNode {
public:
  VectorCastF2XNode(Node* in, const TypeVect* vt) : VectorCastNode(in, vt) {
    assert(in->bottom_type()->is_vect()->element_basic_type() == T_FLOAT, "must be float");
  }
  virtual int Opcode() const;
};

class VectorCastD2XNode : public VectorCastNode {
public:
  VectorCastD2XNode(Node* in, const TypeVect* vt) : VectorCastNode(in, vt) {
    assert(in->bottom_type()->is_vect()->element_basic_type() == T_DOUBLE, "must be double");
  }
  virtual int Opcode() const;
};

class VectorInsertNode : public VectorNode {
 public:
  VectorInsertNode(Node* vsrc, Node* new_val, ConINode* pos, const TypeVect* vt) : VectorNode(vsrc, new_val, (Node*)pos, vt) {
   assert(pos->get_int() >= 0, "positive constants");
   assert(pos->get_int() < (int)vt->length(), "index must be less than vector length");
   assert(Type::cmp(vt, vsrc->bottom_type()) == 0, "input and output must be same type");
  }
  virtual int Opcode() const;
  uint pos() const { return in(3)->get_int(); }

  static Node* make(Node* vec, Node* new_val, int position);
};

#endif // SHARE_VM_OPTO_VECTORNODE_HPP
