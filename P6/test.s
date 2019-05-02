	.data
	.align  2
.L0:	.space  4	# k
	.data
	.align  2
.L1:	.space  4	# a
	.text
_bar:		# METHOD ENTRY
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	addu    $fp, $sp, 8		#set the FP
	subu    $sp, $sp, 0		#push space for locals
	lw      $t0, a		#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $a0, 4($sp)	# load content for write
	li      $v0, 1		# system call number
	syscall
	.data
.L2:	.asciiz	# "hello world\n"
	la      $a0, .L2		# [string] for write
	li      $v0, 4		# system call number
	syscall
	lw      $ra, 0($fp)	#load return address
	move    $t0, $fp		#save control link
	lw      $fp, -4($fp)	#restore FP
	move    $sp, $t0		#restore SP
	jr      $ra		#return
	.text
_branch:		# METHOD ENTRY
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	addu    $fp, $sp, 16		#set the FP
	subu    $sp, $sp, 0		#push space for locals
.L3_Entry:
	lw      $t0, k		#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t0, a		#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop exp1 into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop exp2 into $t0
	addu    $sp, $sp, 4
	seq     $t1, $t1, $t0
	sw      $t1, 0($sp)	#save result on stack
	subu    $sp, $sp, 4
	addu    $t1, 4($sp)	#get condition result in $t1
	li      $t0, 0
	beq     $t1, $t0, .L5_Else
.L4_If:
	li      $t0, 1		#load integer value
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t0, k		#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop exp1 into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop exp2 into $t0
	addu    $sp, $sp, 4
	add     $t1, $t1, $t0
	sw      $t1, 0($sp)	#save result on stack
	subu    $sp, $sp, 4
	la      $t0, 0($fp)	#load addr of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop LHS into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop RHS into $t0
	addu    $sp, $sp, 4
	sw      $t0, 0($t1)	#Store value $t0 at address $t1
.L5_Else:
	.data
.L6:	.asciiz	# "this is else"
	la      $a0, .L6		# [string] for write
	li      $v0, 4		# system call number
	syscall
	lw      $ra, -8($fp)	#load return address
	move    $t0, $fp		#save control link
	lw      $fp, -12($fp)	#restore FP
	move    $sp, $t0		#restore SP
	jr      $ra		#return
	.text
_call:		# METHOD ENTRY
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	addu    $fp, $sp, 8		#set the FP
	subu    $sp, $sp, 4		#push space for locals
	li      $t0, 4		#load integer value
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t0, -8($fp)	#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	jal     _branch		#jump into function
	lw      $ra, 0($fp)	#load return address
	move    $t0, $fp		#save control link
	lw      $fp, -4($fp)	#restore FP
	move    $sp, $t0		#restore SP
	jr      $ra		#return
	.text
_foo:		# METHOD ENTRY
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	addu    $fp, $sp, 16		#set the FP
	subu    $sp, $sp, 8		#push space for locals
	lw      $t0, 4($fp)	#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t0, 4($fp)	#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop exp1 into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop exp2 into $t0
	addu    $sp, $sp, 4
	add     $t1, $t1, $t0
	sw      $t1, 0($sp)	#save result on stack
	subu    $sp, $sp, 4
	la      $t0, -8($fp)	#load addr of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop LHS into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop RHS into $t0
	addu    $sp, $sp, 4
	sw      $t0, 0($t1)	#Store value $t0 at address $t1
	lw      $t0, 8($fp)	#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#[not] pop exp into $t1
	addu    $sp, $sp, 4
	not     $t1, $t1
	sw      $t1, 0($sp)	#save result on stack
	subu    $sp, $sp, 4
	la      $t0, -12($fp)	#load addr of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop LHS into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop RHS into $t0
	addu    $sp, $sp, 4
	sw      $t0, 0($t1)	#Store value $t0 at address $t1
	lw      $ra, -8($fp)	#load return address
	move    $t0, $fp		#save control link
	lw      $fp, -12($fp)	#restore FP
	move    $sp, $t0		#restore SP
	jr      $ra		#return
	.text
	.globl  main
main:		# METHOD ENTRY
__start:		# add __start label for main only
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	addu    $fp, $sp, 8		#set the FP
	subu    $sp, $sp, 16		#push space for locals
	li      $t0, 1		#load integer value
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	la      $t0, -8($fp)	#load addr of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop LHS into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop RHS into $t0
	addu    $sp, $sp, 4
	sw      $t0, 0($t1)	#Store value $t0 at address $t1
	li      $t0, 2		#load integer value
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	la      $t0, -12($fp)	#load addr of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop LHS into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop RHS into $t0
	addu    $sp, $sp, 4
	sw      $t0, 0($t1)	#Store value $t0 at address $t1
	lw      $t0, -12($fp)	#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t0, -8($fp)	#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t0, -12($fp)	#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop exp1 into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop exp2 into $t0
	addu    $sp, $sp, 4
	mult    $t1, $t0
	sw      $t1, 0($sp)	#save result on stack
	subu    $sp, $sp, 4
	lw      $t0, -8($fp)	#load value of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop exp1 into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop exp2 into $t0
	addu    $sp, $sp, 4
	add     $t1, $t1, $t0
	sw      $t1, 0($sp)	#save result on stack
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop exp1 into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop exp2 into $t0
	addu    $sp, $sp, 4
	sub     $t1, $t1, $t0
	sw      $t1, 0($sp)	#save result on stack
	subu    $sp, $sp, 4
	la      $t0, -16($fp)	#load addr of Id
	sw      $t0, 0($sp)
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#pop LHS into $t1
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#pop RHS into $t0
	addu    $sp, $sp, 4
	sw      $t0, 0($t1)	#Store value $t0 at address $t1
	lw      $ra, 0($fp)	#load return address
	move    $t0, $fp		#save control link
	lw      $fp, -4($fp)	#restore FP
	move    $sp, $t0		#restore SP
	li      $v0, 10		#load exit code for syscall
	syscall		#only do this for main
