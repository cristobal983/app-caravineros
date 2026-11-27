package com.fabiancg.persistenciaroomsqlite

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificacionHelper {

    private const val CHANNEL_ID = "esfocar_alertas_channel"
    private const val CHANNEL_NAME = "Alertas ESFOCAR"

    fun crearCanal(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones de documentos subidos y retroalimentacion de pruebas"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun mostrarNotificacion(context: Context, titulo: String, mensaje: String) {
        crearCanal(context)

        val permisoConcedido = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!permisoConcedido) return

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val idNotificacion = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        NotificationManagerCompat.from(context).notify(idNotificacion, builder.build())
    }
}
