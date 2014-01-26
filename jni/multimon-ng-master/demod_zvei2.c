/*
 *      demod_zvei2.c
 *
 *      Copyright (C) 2013
 *          Elias Oenal    (EliasOenal@gmail.com)
 *
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

#define SAMPLE_RATE 22050
#define PHINC(x) ((x)*0x10000/SAMPLE_RATE)

#include "multimon.h"

static const unsigned int zvei2_freq[16] = {
    PHINC(2400), PHINC(1060), PHINC(1160), PHINC(1270),
    PHINC(1400), PHINC(1530), PHINC(1670), PHINC(1830),
    PHINC(2000), PHINC(2200), PHINC(885), PHINC(825),
    PHINC(740), PHINC(680), PHINC(970), PHINC(2600)
};

/* ---------------------------------------------------------------------- */

static void zvei2_init(struct demod_state *s)
{
    selcall_init(s);
}

static void zvei2_deinit(struct demod_state *s)
{
    selcall_deinit(s);
}

static void zvei2_demod(struct demod_state *s, buffer_t buffer, int length)
{
    selcall_demod(s, buffer.fbuffer, length, zvei2_freq, demod_zvei2.name);
}

const struct demod_param demod_zvei2 = {
    "ZVEI2", true, SAMPLE_RATE, 0, zvei2_init, zvei2_demod, zvei2_deinit
};



