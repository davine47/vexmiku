// Generator : SpinalHDL v1.7.1    git head : 0444bb76ab1d6e19f0ec46bc03c4769776deb7d5
// Component : Miku
// Git hash  : 28302e01d532ab6db4e673c20c6983c1bb169146

`timescale 1ns/1ps 
module Miku (
  input               clk,
  input               reset
);

  wire       [31:0]   stage3_DANCE;
  wire       [31:0]   stage2_DANCE;
  wire       [31:0]   stage0_SING;
  wire       [31:0]   stage3_RAP;
  wire       [31:0]   stage4_DANCE;
  wire       [31:0]   stage2_RAP;
  wire       [31:0]   stage1_SING;
  wire       [31:0]   stage1_SingPlugin_aha;
  wire       [31:0]   stage3_RapPlugin_rap;
  wire                when_Pipeline_l102;
  reg        [31:0]   stage0_to_stage1_SING_r;
  wire                when_Pipeline_l102_1;
  reg        [31:0]   stage2_to_stage3_DANCE_r;
  wire                when_Pipeline_l102_2;
  reg        [31:0]   stage3_to_stage4_DANCE_r;
  wire                when_Pipeline_l102_3;
  reg        [31:0]   stage2_to_stage3_RAP_r;

  assign stage3_DANCE = stage2_to_stage3_DANCE_r;
  assign stage2_DANCE = (32'h00000002 + stage2_RAP);
  assign stage0_SING = 32'h00000001;
  assign stage3_RAP = stage2_to_stage3_RAP_r;
  assign stage4_DANCE = stage3_to_stage4_DANCE_r;
  assign stage2_RAP = 32'h0000029a;
  assign stage1_SING = stage0_to_stage1_SING_r;
  assign stage1_SingPlugin_aha = (stage1_SING + 32'h00000001);
  assign stage3_RapPlugin_rap = (stage3_RAP + 32'h00000003);
  assign when_Pipeline_l102 = 1'b1;
  assign when_Pipeline_l102_1 = 1'b1;
  assign when_Pipeline_l102_2 = 1'b1;
  assign when_Pipeline_l102_3 = 1'b1;
  always @(posedge clk) begin
    if(when_Pipeline_l102) begin
      stage0_to_stage1_SING_r <= stage0_SING;
    end
    if(when_Pipeline_l102_1) begin
      stage2_to_stage3_DANCE_r <= stage2_DANCE;
    end
    if(when_Pipeline_l102_2) begin
      stage3_to_stage4_DANCE_r <= stage3_DANCE;
    end
    if(when_Pipeline_l102_3) begin
      stage2_to_stage3_RAP_r <= stage2_RAP;
    end
  end


endmodule
