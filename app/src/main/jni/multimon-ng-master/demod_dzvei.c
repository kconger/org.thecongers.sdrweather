/*
 *      demod_dzvei.c
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

static const unsigned int dzvei_freq[16] = {
    PHINC(2200), PHINC(970), PHINC(1060), PHINC(1160),
    PHINC(1270), PHINC(1400), PHINC(1530), PHINC(1670),
    PHINC(1830), PHINC(2000), PHINC(825), PHINC(740),
    PHINC(2600), PHINC(885), PHINC(2400), PHINC(680)
};

/* ---------------------------------------------------------------------- */

static void dzvei_init(struct demod_state *s)
{
    selcall_init(s);
}

static void dzvei_deinit(struct demod_state *s)
{
    selcall_deinit(s);
}

static void dzvei_demod(struct demod_state *s, buffer_t buffer, int length)
{
    selcall_demod(s, buffer.fbuffer, length, dzvei_freq, demod_dzvei.name);
}

const struct demod_param demod_dzvei = {
    "DZVEI", true, SAMPLE_RATE, 0, dzvei_init, dzvei_demod, dzvei_deinit
};



