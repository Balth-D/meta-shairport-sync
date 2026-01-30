SUMMARY = "AirPlay and AirPlay 2 audio player"
DESCRIPTION = "Shairport Sync is an AirPlay audio player for Linux and FreeBSD. It plays audio streamed from Apple devices and from AirPlay sources such as OwnTone (formerly forked-daapd)."
HOMEPAGE = "https://github.com/mikebrady/shairport-sync"

LICENSE  = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES;md5=9f329b7b34fcd334fb1f8e2eb03d33ff"

inherit autotools pkgconfig python3native update-rc.d systemd

DEPENDS += "avahi alsa-lib popt libconfig soxr libplist libsodium libgcrypt ffmpeg initscripts xxd-native dbus"
DEPENDS += "python3-packaging-native"
RDEPENDS:${PN} += "nqptp"

SRCREV = "3c8ceb7c97c8782903ec48e280023436711e0913"
PV = "+git${SRCPV}"

FILESEXTRAPATHS:append := ":${THISDIR}/files"
SRC_URI = "git://github.com/mikebrady/shairport-sync.git;branch=master;protocol=https \
           file://shairport-sync.d \
           file://shairport-sync.service"

S = "${WORKDIR}/git"

EXTRA_OECONF += "--sysconfdir=/etc --with-alsa --with-soxr --with-avahi --with-ssl=openssl --with-airplay-2 --with-mpris-interface"


do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/shairport-sync.d ${D}${sysconfdir}/init.d/shairport-sync
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}/${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/shairport-sync.service ${D}${systemd_system_unitdir}/shairport-sync.service
    fi
}

INITSCRIPT_NAME = "shairport-sync"
SYSTEMD_SERVICE:${PN} = "shairport-sync.service"
SYSTEMD_AUTO_ENABLE = "enable"

FILES:${PN} += "${systemd_system_unitdir}/shairport-sync.service \
                ${sysconfdir}/init.d/shairport-sync"
